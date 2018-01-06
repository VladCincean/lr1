package parser.lr1;

import grammar.ContextFreeGrammar;
import grammar.FirstAndFollow;
import model.ProductionRuleCFG;
import model.symbol.Nonterminal;
import model.symbol.Symbol;
import model.symbol.Terminal;

import java.util.ArrayList;
import java.util.List;

public class CanonicalCollection {
    private List<State> states;
    private ContextFreeGrammar G;
    private FirstAndFollow ff;

    /**
     * CanonicalCollection constructor
     * @param G - G' extended grammar
     */
    public CanonicalCollection(ContextFreeGrammar G) {
        this.G = G;
        this.ff = new FirstAndFollow(this.G);

        buildCanonicalCollection();
    }

    public List<State> getTheCanonicalCollection() {
        return states;
    }

    /**
     * Builds the canonical collection according to the ColCan_LR(1) algorithm
     * Input:   G'  - the extended grammar of G
     * Output:  C   - the canonical collection
     */
    private void buildCanonicalCollection() {
        // TODO: this

        List<State> C1 = new ArrayList<>();

        ProductionRuleCFG p0 = null;
        for (ProductionRuleCFG p : G.getProductionRules()) {
            if (p.getId() == 0) {
                p0 = p;
                break;
            }
        }

        State S0 = closure(new Element(p0, 0, Terminal.getDollarSymbol()));
        C1.add(S0);

        boolean changes = true;
        do {
            changes = false;

            // for all s in C1 do
            List<State> toAddInC1 = new ArrayList<>();
            for (State s : C1) {
                // for all X nonterminal or terminal do
                for (Nonterminal X : G.getNonterminals()) {
                    State T = goto1(s, X);
                    toAddInC1.add(T);
                }
                for (Terminal X : G.getTerminals()) {
                    State T = goto1(s, X);
                    toAddInC1.add(T);
                }
            }

            for (State T : toAddInC1) {
                // if T not empty and T not in C1 then add it to C1
                if ((T.getElements().size() > 0) && (!C1.contains(T))) {
                    changes = true;
                    C1.add(T);
                }
            }

        } while (changes);

        this.states = C1;
    }

    /**
     * Goto function
     * goto1(s, X) = closure({[A -> alpha X . beta] | [A -> alpha . X beta] in s})
     * @param s - old state
     * @param X - symbol
     * @return  new state
     */
    private State goto1(State s, Symbol X) {
        //  TODO: !!!!
        //  TODO: something is wrong here, figure out what
        //  TODO: !!!!
        State s1 = new State();

        for (Element e : s.getElements()) {
            //  if [A -> alpha . X beta, u] in s
            //      then add [A -> alpha X . beta, u] in s1

            if (e.getDot() >= e.getCore().getRight().size()) {
                if (e.getDot() > e.getCore().getRight().size()) {
                    System.err.println("CanonicalCollection::closure - Something is wrong :(");
                }

                continue;
            }

            if (e.getCore().getRight().get(e.getDot()).equals(X)) {
                s1.addElement(new Element(e.getCore(), e.getDot() + 1, e.getPrediction()));
            }
        }

        return closure(s1);
    }

    // TODO: test this
    private State closure(State s) {
        State C = new State();

        for (Element I : s.getElements()) {
            State C1 = closure(I);
            for (Element e : C1.getElements()) {
                if (!C.getElements().contains(e)) {
                    C.addElement(e);
                }
            }
        }

        return C;
    }

    /**
     * Closure algorithm.
     * TODO: test this
     * @param I - analysis element
     * @return closure(I)
     * Input:   I   - analysis element
     *          G'  - the extended grammar of G
     *          FIRST(X), for all symbol X
     * Output:  closure(I)
     */
    private State closure(Element I) {
        State C1 = new State();

        C1.addElement(I);

        boolean changes = true;
        do {
            changes = false;

            // for all [A -> alpha . B beta, a] in C1 do
            List<Element> newElements = new ArrayList<>();
            for (Element e : C1.getElements()) {
                if (e.getDot() >= e.getCore().getRight().size()) {
                    if (e.getDot() > e.getCore().getRight().size()) {
                        System.err.println("CanonicalCollection::closure - Something is wrong :(");
                    }

                    continue;
                }

                if (e.getCore().getRight().get(e.getDot()) instanceof Nonterminal) {
                    Nonterminal A = e.getCore().getLeft();
                    List<Symbol> alpha = e.getCore().getRight().subList(0, e.getDot());
                    Nonterminal B = (Nonterminal) e.getCore().getRight().get(e.getDot());
                    List<Symbol> beta = e.getCore().getRight().subList(e.getDot() + 1, e.getCore().getRight().size());
                    Terminal a = e.getPrediction();

                    // for all "B -> gamma" production rule do
                    for (ProductionRuleCFG p : G.getProductionRules()) {
                        if (p.getLeft().equals(B)) {
                            List<Symbol> gamma = p.getRight();

                            // for all b in FIRST(beta alpha) do
                            List<Symbol> betaAlpha = new ArrayList<>();
                            betaAlpha.addAll(beta);
                            betaAlpha.addAll(alpha);
                            for (Terminal b : ff.getFirstOfSequence(betaAlpha)) {
                                // if [B -> . gamma, b] not in C1 then add it
                                newElements.add(new Element(p, 0, b));
                            }
                        }
                    }
                }
            }

            for (Element e : newElements) {
                if (!C1.getElements().contains(e)) {
                    changes = true;
                    C1.addElement(e);
                }
            }
        } while (changes);

        return C1;
    }
}
