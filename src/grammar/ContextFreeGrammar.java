package grammar;

import model.ProductionRuleCFG;
import model.symbol.Nonterminal;
import model.symbol.Terminal;

import java.util.List;

public class ContextFreeGrammar {
    private List<Nonterminal> nonterminals;
    private List<Terminal> terminals;
    private List<ProductionRuleCFG> productionRules;
    private Nonterminal startSymbol;

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
