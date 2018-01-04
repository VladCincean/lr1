import grammar.ContextFreeGrammar;
import grammar.GrammarBuilder;
import model.symbol.Symbol;
import model.symbol.Terminal;

import java.util.Map;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        ContextFreeGrammar cfg = GrammarBuilder.buildFromFile("res/grammar4.txt");

        Map<Symbol, Set<Terminal>> first = cfg.getFirst1();
        Map<Symbol, Set<Terminal>> follow = cfg.getFollow1();

        System.out.println(first.toString());
        System.out.println(follow.toString());
    }
}
