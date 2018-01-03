package model;

import model.symbol.Nonterminal;
import model.symbol.Symbol;

import java.util.List;

public class ProductionRuleCFG {
    private int id;
    private Nonterminal left;
    private List<Symbol> right;

    public ProductionRuleCFG(int id, Nonterminal left, List<Symbol> right) {
        this.id = id;
        this.left = left;
        this.right = right;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Nonterminal getLeft() {
        return left;
    }

    public void setLeft(Nonterminal left) {
        this.left = left;
    }

    public List<Symbol> getRight() {
        return right;
    }

    public void setRight(List<Symbol> right) {
        this.right = right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductionRuleCFG that = (ProductionRuleCFG) o;

        if (id != that.id) return false;
        if (!left.equals(that.left)) return false;
        return right.equals(that.right);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + left.hashCode();
        result = 31 * result + right.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Symbol s : right) {
            sb.append(" ");
            sb.append(s.getSymbol());
        }

        return left + " ->" + sb.toString();
    }
}
