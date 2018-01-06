package parser.lr1;

import grammar.ContextFreeGrammar;
import model.symbol.Symbol;
import model.symbol.Terminal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {
    private Map<State, Map<Symbol, Action>> T;
    private CanonicalCollection canonicalCollection;
    private ContextFreeGrammar G;

    /**
     * Constructor for the LR(1) analysis table
     * @param G - G' the extended grammar of a CFG
     */
    public Table(ContextFreeGrammar G) {
        this.G = G;
        this.canonicalCollection = new CanonicalCollection(G);
        buildTheTable();
    }

    public Action get(State s, Symbol a) {
        return T.get(s).get(a);
    }

//    public Map<State, Map<Symbol, Action>> getT() {
//        return T;
//    }

    private void buildTheTable() {
        this.T = new HashMap<>();
        for (State si : canonicalCollection.getTheCanonicalCollection()) {
            Map<Symbol, Action> R = getActionsForState(si);
            T.put(si, R);
        }
    }

    private Map<Symbol, Action> getActionsForState(State si) {
        Map<Symbol, Action> R = new HashMap<>();
        List<Symbol> symbols = new ArrayList<>();

        symbols.addAll(G.getNonterminals());
        symbols.addAll(G.getTerminals());
        symbols.add(Terminal.getDollarSymbol());

        // pre-fill everything with ERROR; later it will be replaced
        for (Symbol a : symbols) {
            R.put(a, new Action(ActionType.ERROR));
        }

        for (Symbol a : symbols) {
            // for all [A -> alpha . a beta, u] in si
            for (Element e : si.getElements()) {
                // having the form [A -> alpha . X beta, u]
                if (e.getDot() < e.getCore().getRight().size()) {
                    // [A -> alpha . a beta, u]
                    if (e.getCore().getRight().get(e.getDot()).equals(a)) {
                        // if goto(si, a) = sj then
                        // action(si, a) = shift j
                        int j = canonicalCollection.getGoto1Index(si, a);
                        R.put(a, new Action(ActionType.SHIFT, j));
                    }
                }
                // having the form [A -> beta ., u]
                else if (e.getDot() == e.getCore().getRight().size()) {
                    // if A != S', then action(si, u) = reduce l
                    //      where l is the number of the production A -> beta
                    if (!e.getCore().getLeft().equals(G.getStartSymbol())) {
                        Symbol u = e.getPrediction();
                        int l = e.getCore().getId();

                        R.put(u, new Action(ActionType.REDUCE, l));
                    }
                    // if [S' -> S., $] in si, then action(si, $) = accept
                    else {
                        R.put(Terminal.getDollarSymbol(), new Action(ActionType.ACCEPT));
                    }
                }
            }
        }

        return R;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        List<Symbol> symbols = new ArrayList<>();

        symbols.addAll(G.getNonterminals());
        symbols.addAll(G.getTerminals());
        symbols.add(Terminal.getDollarSymbol());

        sb.append("| state | ");
        for (Symbol X : symbols) {
            sb.append(X.getSymbol());
            sb.append(" | ");
        }
        sb.append("\n---------\n");
        for (int i = 0; i < canonicalCollection.getTheCanonicalCollection().size(); i++) {
            State I = canonicalCollection.getTheCanonicalCollection().get(i);
            sb.append("| ");
            sb.append(Integer.toString(i));
            sb.append(" | ");
            for (Symbol X : symbols) {
                sb.append(T.get(I).get(X));
                sb.append(" | ");
            }
            sb.append("\n");
        }
        sb.append("---------\n");

        return sb.toString();
    }
}
