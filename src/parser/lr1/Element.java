package parser.lr1;

import model.ProductionRuleCFG;
import model.symbol.Terminal;

public class Element {
    private ProductionRuleCFG core;
    private int dot;
    private Terminal prediction;

    public Element(ProductionRuleCFG core, int dot, Terminal prediction) {
        this.core = core;
        this.dot = dot;
        this.prediction = prediction;
    }

    public ProductionRuleCFG getCore() {
        return core;
    }

    public void setCore(ProductionRuleCFG core) {
        this.core = core;
    }

    public int getDot() {
        return dot;
    }

    public void setDot(int dot) {
        this.dot = dot;
    }

    public Terminal getPrediction() {
        return prediction;
    }

    public void setPrediction(Terminal prediction) {
        this.prediction = prediction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Element element = (Element) o;

        if (dot != element.dot) return false;
        if (!core.equals(element.core)) return false;
        return prediction.equals(element.prediction);
    }

    @Override
    public int hashCode() {
        int result = core.hashCode();
        result = 31 * result + dot;
        result = 31 * result + prediction.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(core.getLeft().getSymbol());
        sb.append(" ->");
        for (int i = 0; i < core.getRight().size(); i++) {
            if (i == dot) {
                sb.append(" .");
            }
            sb.append(" ");
            sb.append(core.getRight().get(i).getSymbol());
        }
        sb.append(", ");
        sb.append(prediction.getSymbol());
        sb.append("]");

        return sb.toString();
    }
}
