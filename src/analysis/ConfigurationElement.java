package analysis;

import model.symbol.Symbol;
import parser.lr1.State;

import java.util.List;

public class ConfigurationElement {
    State state;
    Symbol symbol;

    public ConfigurationElement(Symbol symbol, State state) {
        this.state = state;
        this.symbol = symbol;
    }

    public State getState() {
        return state;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return "ConfigurationElement{" +
                "state=" + state +
                ", symbol=" + symbol +
                '}';
    }
}
