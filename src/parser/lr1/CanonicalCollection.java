package parser.lr1;

import grammar.ContextFreeGrammar;
import model.symbol.Symbol;

import java.util.ArrayList;
import java.util.List;

public class CanonicalCollection {
    private List<State> states;
    private ContextFreeGrammar G;

    /**
     * CanonicalCollection constructor
     * @param G - G' extended grammar
     */
    public CanonicalCollection(ContextFreeGrammar G) {
        this.G = G;
        this.states = new ArrayList<>();

        buildCanonicalCollection();
    }

    /**
     * Builds the canonical collection according to the ColCan_LR(1) algorithm
     * Input:   G'  - the extended grammar of G
     * Output:  C   - the canonical collection
     */
    private void buildCanonicalCollection() {
        // TODO: this
    }

    private State goto1(State oldState, Symbol s) {
        State newState = null;

        // TODO: this

        return newState;
    }

    /**
     * Closure algorithm.
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
            // TODO: continue to implement from here
        } while (changes);

        return C1;
    }
}
