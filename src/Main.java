import grammar.ContextFreeGrammar;
import grammar.FirstAndFollow;
import grammar.GrammarBuilder;
import model.symbol.Symbol;
import model.symbol.Terminal;

import java.util.Map;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        ContextFreeGrammar cfg = GrammarBuilder.buildFromFile("res/grammar4.txt");
        FirstAndFollow ff = new FirstAndFollow(cfg);

        Map<Symbol, Set<Terminal>> first = ff.getFirst1();
        Map<Symbol, Set<Terminal>> follow = ff.getFollow1();

        System.out.println(first.toString());
        System.out.println(follow.toString());
    }
}
