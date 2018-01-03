package model.symbol;

public class Terminal implements Symbol {
    private String symbol;

    public static Terminal getEpsilonSymbol() {
        return new Terminal("ε");
    }

    public static Terminal getDollarSymbol() {
        return new Terminal("$");
    }

    public Terminal(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Terminal terminal = (Terminal) o;

        return symbol.equals(terminal.symbol);
    }

    @Override
    public int hashCode() {
        return symbol.hashCode();
    }

    @Override
    public String toString() {
        return symbol;
    }
}
