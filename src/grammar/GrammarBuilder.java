package grammar;

import model.ProductionRuleCFG;
import model.symbol.Nonterminal;
import model.symbol.Symbol;
import model.symbol.Terminal;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public abstract class GrammarBuilder {

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

    /**
     * Transforms a context free grammar G to the extended grammar G'
     * @param G - input CFG
     * Input:   G   - a context free grammar
     * Output:  G'  - G's extended grammar
     */
    public static void transformToExtendedGrammar(ContextFreeGrammar G) {
        Nonterminal S = G.getStartSymbol();
        Nonterminal S1 = new Nonterminal(S.getSymbol() + "'");
        List<Symbol> rhs = new ArrayList<>();
        rhs.add(S);
        ProductionRuleCFG p = new ProductionRuleCFG(0, S1, rhs);

        G.getNonterminals().add(S1);
        G.getProductionRules().add(0, p);
        G.setStartSymbol(S1);
    }
}
