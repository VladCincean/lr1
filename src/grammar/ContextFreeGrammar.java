package grammar;

import model.ProductionRuleCFG;
import model.symbol.Nonterminal;
import model.symbol.Symbol;
import model.symbol.Terminal;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class ContextFreeGrammar {
    private List<Nonterminal> nonterminals;
    private List<Terminal> terminals;
    private List<ProductionRuleCFG> productionRules;
    private Nonterminal startSymbol;

    public static ContextFreeGrammar buildFromFile(String filename) {
        // TODO: this

        List<Nonterminal> nonterminals = new ArrayList<>();
        List<Terminal> terminals = new ArrayList<>();
        List<ProductionRuleCFG> productionRules = new ArrayList<>();
        Nonterminal startSymbol = null;

        try(
                FileInputStream fin = new FileInputStream(filename);
                BufferedReader reader = new BufferedReader(new InputStreamReader(fin))
        ) {
            String line = null;

            int numberOfNonterminals = 0;
            int numberOfTerminals = 0;
            int numberOfProductionRules = 0;

            // read number of non-terminals
            line = reader.readLine();
            numberOfNonterminals = Integer.parseInt(line);

            // read the non-terminals
            for (int i = 0; i < numberOfNonterminals; i++) {
                line = reader.readLine().trim();

                nonterminals.add(new Nonterminal(line));
            }

            // read the number of terminals
            line = reader.readLine();
            numberOfTerminals = Integer.parseInt(line);

            // read the terminals
            for (int i = 0; i < numberOfTerminals; i++) {
                line = reader.readLine().trim();

                terminals.add(new Terminal(line));
            }

            // add epsilon dummy terminal
            if (!terminals.contains(Terminal.getEpsilonSymbol())) {
                terminals.add(Terminal.getEpsilonSymbol());
            }

            // read the number of production rules
            line = reader.readLine();
            numberOfProductionRules = Integer.parseInt(line);

            // read the production rules
            for (int i = 0; i < numberOfProductionRules; i++) {
                line = reader.readLine().trim();

                String[] parts = line.split(" ");

                if (i == 0) {
                    startSymbol = new Nonterminal(parts[0]);
                }

                Nonterminal left = new Nonterminal(parts[0]);
                List<Symbol> right = new ArrayList<>();
                for (int j = 1; j < parts.length; j++) {
                    if (nonterminals.contains(new Nonterminal(parts[j]))) {
                        right.add(new Nonterminal(parts[j]));
                    } else if (terminals.contains(new Terminal(parts[j]))) {
                        right.add(new Terminal(parts[j]));
                    } else {
                        System.err.println("Error. Invalid grammar file format in production rule " + Integer.toString(i));
                        return null;
                    }
                }

                productionRules.add(new ProductionRuleCFG(i + 1, left, right));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ContextFreeGrammar(nonterminals, terminals, productionRules, startSymbol);
    }

    public ContextFreeGrammar(
            List<Nonterminal> nonterminals,
            List<Terminal> terminals,
            List<ProductionRuleCFG> productionRules,
            Nonterminal startSymbol
    ) {
        this.nonterminals = nonterminals;
        this.terminals = terminals;
        this.productionRules = productionRules;
        this.startSymbol = startSymbol;
    }

    private static Map<Symbol, Set<Terminal>> deepCopyMap(
            Map<Symbol, Set<Terminal>> original
    ) {
        Map<Symbol, Set<Terminal>> copy = new HashMap<>();
        for (Map.Entry<Symbol, Set<Terminal>> entry : original.entrySet()) {
            copy.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }

        return copy;
    }

    private Set<Terminal> concatenationLength1Util(Set<Terminal> L1, Set<Terminal> L2) {
        Set<Terminal> result = new HashSet<>();

        result.addAll(L1);

        if (!L1.contains(Terminal.getEpsilonSymbol())) {
            return result;
        }

        result.remove(Terminal.getEpsilonSymbol());
        result.addAll(L2);

        return result;
    }

    public Map<Symbol, Set<Terminal>> getFirst1() {
        Map<Symbol, Set<Terminal>> F0 = null;
        Map<Symbol, Set<Terminal>> F1 = new HashMap<>();

        // pre-init
        for (Nonterminal A : nonterminals) {
            F1.put(A, new HashSet<>());
        }
        for (Terminal a : terminals) {
            F1.put(a, new HashSet<>());
        }

        // for every a terminal, F1(a) = {a}
        for (Terminal a : terminals) {
            F1.get(a).add(a);
        }

        // for every A nonterminal, F1(A) = {a terminal | "A -> a alpha" production} U {ε | "A -> ε" production}
        for (Nonterminal A : nonterminals) {
            // toate productiile
            for (ProductionRuleCFG p : productionRules) {
                // toate productiile de forma A -> ...
                if (p.getLeft().equals(A)) {
                    if (p.getRight().get(0) instanceof Terminal) { // cazul cu epsilon intra tot aici
                        Terminal a = (Terminal) p.getRight().get(0);
                        F1.get(A).add(a);
                    }
                }
            }
        }

        do {
            F0 = deepCopyMap(F1);

            // for every A nonterminal do:
            //      F1(A) = { a terminal or epsilon | A -> X1X2...Xk, a in F0(X1) + ... + F0(Xk) }
            //      where "+" = concatenation of length 1 operator
            for (Nonterminal A : nonterminals) {
                F1.replace(A, new HashSet<>());

                // toate productiile
                for (ProductionRuleCFG p : productionRules) {
                    // toate productiile de forma A -> ...
                    if (p.getLeft().equals(A)) {
                        Set<Terminal> F1A = new HashSet<>();
                        F1A.add(Terminal.getEpsilonSymbol());

                        for (Symbol s : p.getRight()) {
                            F1A = concatenationLength1Util(F1A, F0.get(s));
                        }

//                        F1.replace(A, F1A);
                        F1.get(A).addAll(F1A);
                    }
                }
            }
        } while (!F1.equals(F0));

        return F1;
    }

    public Map<Symbol, Set<Terminal>> getFollow1() {
        Map<Symbol, Set<Terminal>> first = getFirst1();
        Map<Symbol, Set<Terminal>> follow = new HashMap<>();

        // init
        for (Nonterminal X : nonterminals) {
            follow.put(X, new HashSet<>());
        }
        follow.get(startSymbol).add(Terminal.getDollarSymbol());

        // main algorithm
        boolean changes = true;
        do {
            changes = false;

            // for each non-terminal B, where "A -> alpha B beta" production rule, do:
            // FOLLOW(A) U= (FIRST(beta) - {epsilon})
            // if epsilon in FIRST1(beta) then
            //      FOLLOW(B) U= FOLLOW(A)
            for (ProductionRuleCFG p : productionRules) {
                Nonterminal A = p.getLeft();

                // I. beta != epsilon
                for (int i = 0; i < p.getRight().size() - 1; i++) {
                    if (p.getRight().get(i) instanceof Nonterminal) {
                        Nonterminal B = (Nonterminal) p.getRight().get(i);

                        Set<Terminal> firstBeta = new HashSet<>();
                        firstBeta.addAll(first.get(p.getRight().get(i + 1)));

                        if (firstBeta.contains(Terminal.getEpsilonSymbol())) {
                            if (!follow.get(B).containsAll(follow.get(A))) {
                                changes = true;
                            }
                            follow.get(B).addAll(follow.get(A));
                        }

                        firstBeta.remove(Terminal.getEpsilonSymbol());
                        if (!follow.get(B).containsAll(firstBeta)) {
                            changes = true;
                        }
                        follow.get(B).addAll(firstBeta);
                    }
                }

                // II. beta = epsilon
                if (p.getRight().size() > 0) {
                    if (p.getRight().get(p.getRight().size() - 1) instanceof Nonterminal) {
                        Nonterminal B = (Nonterminal) p.getRight().get(p.getRight().size() - 1);

                        Set<Terminal> firstBeta = new HashSet<>();
                        firstBeta.addAll(first.get(Terminal.getEpsilonSymbol()));

                        if (firstBeta.contains(Terminal.getEpsilonSymbol())) {
                            if (!follow.get(B).containsAll(follow.get(A))) {
                                changes = true;
                            }
                            follow.get(B).addAll(follow.get(A));
                        } else {
                            System.err.println("Something is wrong :(");
                        }

                        // useless code
                        firstBeta.remove(Terminal.getEpsilonSymbol());
                        if (!follow.get(B).containsAll(firstBeta)) {
                            changes = true;
                        }
                        follow.get(B).addAll(firstBeta);
                    }
                }
            }
        } while (changes);

        return follow;
    }

    public List<Nonterminal> getNonterminals() {
        return nonterminals;
    }

    public void setNonterminals(List<Nonterminal> nonterminals) {
        this.nonterminals = nonterminals;
    }

    public List<Terminal> getTerminals() {
        return terminals;
    }

    public void setTerminals(List<Terminal> terminals) {
        this.terminals = terminals;
    }

    public List<ProductionRuleCFG> getProductionRules() {
        return productionRules;
    }

    public void setProductionRules(List<ProductionRuleCFG> productionRules) {
        this.productionRules = productionRules;
    }

    public Nonterminal getStartSymbol() {
        return startSymbol;
    }

    public void setStartSymbol(Nonterminal startSymbol) {
        this.startSymbol = startSymbol;
    }

    @Override
    public String toString() {
        return "ContextFreeGrammar{" +
                "nonterminals=" + nonterminals +
                ", terminals=" + terminals +
                ", productionRules=" + productionRules +
                ", startSymbol=" + startSymbol +
                '}';
    }
}
