package analysis;

import grammar.ContextFreeGrammar;
import model.ProductionRuleCFG;
import model.symbol.Symbol;
import model.symbol.Terminal;
import parser.lr1.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Analyser {
    ContextFreeGrammar cfg;
    Table table;
    String sequence;
    List<ProductionRuleCFG> productionRuleCFG;
    CanonicalCollection canCol;

    public Analyser(ContextFreeGrammar cfg, Table table, String sequence, List<ProductionRuleCFG> productionRuleCFG, CanonicalCollection canCol) {
        this.cfg = cfg;
        this.table = table;
        this.sequence = sequence;
        this.productionRuleCFG = productionRuleCFG;
        this.canCol = canCol;
    }

    public void analyse()
    {
        ConfigurationElement alphaInit = new ConfigurationElement(new Terminal("$"),
                canCol.getTheCanonicalCollection().get(0));
        List<ConfigurationElement> alpha = new ArrayList<ConfigurationElement>();
        alpha.add(alphaInit);

        List<String> symbolList = new ArrayList<String>(Arrays.asList(sequence.split(",")));
        List<Symbol> beta = new ArrayList<Symbol>();
        for(String s:symbolList)
        {
            beta.add(new Terminal(s));
        }
        beta.add(new Terminal("$"));

        Configuration configuration = new Configuration(
                alpha, beta, new ArrayList<Integer>(), table, productionRuleCFG, canCol);

        Action crtAction = table.get(canCol.getTheCanonicalCollection().get(0), beta.get(0));

        while(!crtAction.getActionType().equals(ActionType.ACCEPT) && !crtAction.getActionType().equals(ActionType.ERROR))
        {
            try
            {
                System.out.println(configuration);
                System.out.println(crtAction);
                if(crtAction.getActionType().equals(ActionType.SHIFT))
                {
                    configuration = configuration.shift(crtAction.getParameter());
                }
                else
                {
                    configuration = configuration.reduce(crtAction.getParameter());
                }
                crtAction = table.get(configuration.getAlpha().get(configuration.getAlpha().size() - 1).getState(), configuration.getBeta().get(0));
            }
            catch(Error e)
            {
                System.out.println(e.getMessage());
            }
        }
        if(crtAction.getActionType().equals(ActionType.ACCEPT))
        {
            System.out.println("sequence accepted");
        }
        else
        {
            System.out.println("sequence not accepted");
        }
    }

}
