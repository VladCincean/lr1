package analysis;

import model.ProductionRuleCFG;
import model.symbol.Symbol;
import parser.lr1.Action;
import parser.lr1.CanonicalCollection;
import parser.lr1.State;
import parser.lr1.Table;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.ErrorManager;
import java.util.stream.Collectors;

public class Configuration {
    List<ConfigurationElement> alpha;
    List<Symbol> beta;
    List<Integer> pi;
    Table table;
    List<ProductionRuleCFG> productionRuleCFG;
    CanonicalCollection canCol;

    public Configuration(List<ConfigurationElement> alpha, List<Symbol> beta, List<Integer> pi, Table table,
                         List<ProductionRuleCFG> productionRuleCFG, CanonicalCollection canCol) {
        this.alpha = alpha;
        this.beta = beta;
        this.pi = pi;
        this.table = table;
        this.productionRuleCFG = productionRuleCFG;
        this.canCol = canCol;
    }

    Configuration shift(int idx){
        List<ConfigurationElement> newAlpha = new ArrayList<ConfigurationElement>();
        newAlpha.addAll(alpha);

        //Action crtAction = table.get(alpha.get(alpha.size() - 1).getState(), beta.get(0));

        State nextState = canCol.getTheCanonicalCollection().get(idx);
        newAlpha.add(new ConfigurationElement(beta.get(0), nextState));

        List<Symbol> newBeta = new ArrayList<Symbol>();
        newBeta.addAll(beta);
        newBeta.remove(0);

        return new Configuration(newAlpha, newBeta, pi, table, productionRuleCFG, canCol);
    }

    Configuration reduce(int idx){
        List<ConfigurationElement> newAlpha = new ArrayList<ConfigurationElement>();
        for(int  i = 0; i < alpha.size(); ++i)
        {
            newAlpha.add(alpha.get(i));
        }

        ProductionRuleCFG reduceRule = productionRuleCFG.get(idx);
        List<Symbol> right = new ArrayList<Symbol>(reduceRule.getRight());

        while(0 != right.size())
        {
            if(0 == newAlpha.size())
            {
                throw new Error("error at " + beta.toString());
            }

            Symbol crtSymbol = newAlpha.get(newAlpha.size() - 1).getSymbol();
            if(!right.get(right.size() - 1).equals(crtSymbol))
            {
                throw new Error("error at " + beta.toString());
            }
            right.remove(right.size() - 1);
            newAlpha.remove(newAlpha.size() - 1);
        }

        newAlpha.add(new ConfigurationElement(reduceRule.getLeft(),
                canCol.getTheCanonicalCollection().get(canCol.getGoto1Index(newAlpha.get(newAlpha.size() - 1).getState(), reduceRule.getLeft()))));

        List<Integer> newPi = new ArrayList<Integer>(pi);
        newPi.add(0, idx);
        return new Configuration(newAlpha, beta, newPi, table, productionRuleCFG, canCol);
    }

    public List<ConfigurationElement> getAlpha() {
        return alpha;
    }

    public List<Symbol> getBeta() {
        return beta;
    }

    public List<Integer> getPi() {
        return pi;
    }

    public Table getTable() {
        return table;
    }

    public List<ProductionRuleCFG> getProductionRuleCFG() {
        return productionRuleCFG;
    }

    public CanonicalCollection getCanCol() {
        return canCol;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "alpha=" + alpha +
                ", beta=" + beta +
                ", pi=" + pi +
                '}';
    }
}
