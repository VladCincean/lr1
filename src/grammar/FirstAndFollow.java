package grammar;

import model.ProductionRuleCFG;
import model.symbol.Nonterminal;
import model.symbol.Symbol;
import model.symbol.Terminal;

import java.util.*;

public class FirstAndFollow {
    private ContextFreeGrammar G;
    private Map<Symbol, Set<Terminal>> FIRST1;
    private Map<Symbol, Set<Terminal>> FOLLOW1;

    public FirstAndFollow(ContextFreeGrammar g) {
        G = g;
        buildFirst1();
        buildFollow1();
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

    private void buildFirst1() {
        Map<Symbol, Set<Terminal>> F0 = null;
        Map<Symbol, Set<Terminal>> F1 = new HashMap<>();

        // pre-init
        for (Nonterminal A : G.getNonterminals()) {
            F1.put(A, new HashSet<>());
        }
        for (Terminal a : G.getTerminals()) {
            F1.put(a, new HashSet<>());
        }

        // for every a terminal, F1(a) = {a}
        for (Terminal a : G.getTerminals()) {
            F1.get(a).add(a);
        }

        // for every A nonterminal, F1(A) = {a terminal | "A -> a alpha" production} U {ε | "A -> ε" production}
        for (Nonterminal A : G.getNonterminals()) {
            // toate productiile
            for (ProductionRuleCFG p : G.getProductionRules()) {
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
            for (Nonterminal A : G.getNonterminals()) {
                F1.replace(A, new HashSet<>());

                // toate productiile
                for (ProductionRuleCFG p : G.getProductionRules()) {
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

        Set<Terminal> aaa = new HashSet<>();
        aaa.add(Terminal.getDollarSymbol());
        F1.put(Terminal.getDollarSymbol(), aaa);

        this.FIRST1 = F1;
    }

    private void buildFollow1() {
        Map<Symbol, Set<Terminal>> follow = new HashMap<>();

        // init
        for (Nonterminal X : G.getNonterminals()) {
            follow.put(X, new HashSet<>());
        }
        follow.get(G.getStartSymbol()).add(Terminal.getDollarSymbol());

        // main algorithm
        boolean changes = true;
        do {
            changes = false;

            // for each non-terminal B, where "A -> alpha B beta" production rule, do:
            // FOLLOW(A) U= (FIRST(beta) - {epsilon})
            // if epsilon in FIRST1(beta) then
            //      FOLLOW(B) U= FOLLOW(A)
            for (ProductionRuleCFG p : G.getProductionRules()) {
                Nonterminal A = p.getLeft();

                // I. beta != epsilon
                for (int i = 0; i < p.getRight().size() - 1; i++) {
                    if (p.getRight().get(i) instanceof Nonterminal) {
                        Nonterminal B = (Nonterminal) p.getRight().get(i);

                        Set<Terminal> firstBeta = new HashSet<>();
                        firstBeta.addAll(this.FIRST1.get(p.getRight().get(i + 1)));

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
                        firstBeta.addAll(this.FIRST1.get(Terminal.getEpsilonSymbol()));

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

        this.FOLLOW1 = follow;
    }

    public ContextFreeGrammar getG() {
        return G;
    }

    public Map<Symbol, Set<Terminal>> getFirst1() {
        return FIRST1;
    }

    public Map<Symbol, Set<Terminal>> getFollow1() {
        return FOLLOW1;
    }

    /**
     * TODO: double check and test the correctness of this
     * @param alpha - input sequence
     * @return FIRST(alpha)
     */
    public Set<Terminal> getFirstOfSequence(List<Symbol> alpha) {
        Set<Terminal> firstAlpha = new HashSet<>();

        firstAlpha.add(Terminal.getEpsilonSymbol());
        for (Symbol s : alpha) {
            firstAlpha = concatenationLength1Util(firstAlpha, this.FIRST1.get(s));
        }

        return firstAlpha;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FirstAndFollow that = (FirstAndFollow) o;

        if (!G.equals(that.G)) return false;
        if (!FIRST1.equals(that.FIRST1)) return false;
        return FOLLOW1.equals(that.FOLLOW1);
    }

    @Override
    public int hashCode() {
        int result = G.hashCode();
        result = 31 * result + FIRST1.hashCode();
        result = 31 * result + FOLLOW1.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "FirstAndFollow{" +
                "G=" + G +
                ", FIRST1=" + FIRST1 +
                ", FOLLOW1=" + FOLLOW1 +
                '}';
    }
}
