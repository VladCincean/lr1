package parser.lr1;

import java.util.ArrayList;
import java.util.List;

public class State {
    List<Element> elements;

    public State() {
        elements = new ArrayList<>();
    }

    public void addElement(Element element) {
        if (element == null) {
            System.err.println("State::addElement - null element given");
            return;
        }

        // do not add the element if it already exists
        for (Element e : elements) {
            if (e.equals(element)) {
                System.err.println("State::addElement - element already exists");
                return;
            }
        }

        this.elements.add(element);
    }

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        State state = (State) o;

        return elements.equals(state.elements);
    }

    @Override
    public int hashCode() {
        return elements.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("State{");
        for (int i = 0; i < elements.size(); i++) {
            sb.append(elements.get(i).toString());
            if (i != elements.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("}");

        return sb.toString();
    }
}
