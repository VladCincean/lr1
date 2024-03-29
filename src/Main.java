import analysis.Analyser;
import grammar.ContextFreeGrammar;
import grammar.FirstAndFollow;
import grammar.GrammarBuilder;
import model.symbol.Symbol;
import model.symbol.Terminal;
import parser.lr1.CanonicalCollection;
import parser.lr1.State;
import parser.lr1.Table;

import java.util.Map;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        ContextFreeGrammar cfg = GrammarBuilder.buildFromFile("res/grammar8.txt");
        GrammarBuilder.transformToExtendedGrammar(cfg);
        FirstAndFollow ff = new FirstAndFollow(cfg);

        Map<Symbol, Set<Terminal>> first = ff.getFirst1();
        Map<Symbol, Set<Terminal>> follow = ff.getFollow1();

        System.out.println(first.toString());
        System.out.println(follow.toString());

        CanonicalCollection canCol = new CanonicalCollection(cfg);
        int i = 0;
        for (State s : canCol.getTheCanonicalCollection()) {
            System.out.println(Integer.toString(i) + ": " + s.toString());
            i++;
        }

        Table table = new Table(cfg);
        System.out.println(table.toString());

        StringBuilder sb = new StringBuilder();
        sb.append("INT,MAIN,OPEN_PARA,CLOSE_PARA,OPEN_BRACE,INT,IDENTIFIER,CLOSE_BRACE");

        Analyser analyser = new Analyser(cfg, table, sb.toString(), cfg.getProductionRules(), canCol);
        analyser.analyse();
    }
}
