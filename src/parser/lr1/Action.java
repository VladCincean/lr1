package parser.lr1;

public class Action {
    private ActionType actionType;
    private int parameter;

    public Action(ActionType actionType, int parameter) {
        this.actionType = actionType;
        this.parameter = parameter;
    }

    public Action(ActionType actionType) {
        this.actionType = actionType;
        this.parameter = -1;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public int getParameter() {
        return parameter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Action action = (Action) o;

        if (parameter != action.parameter) return false;
        return actionType == action.actionType;
    }

    @Override
    public int hashCode() {
        int result = actionType.hashCode();
        result = 31 * result + parameter;
        return result;
    }

    @Override
    public String toString() {
        String s = "/";

        switch (actionType) {
            case SHIFT:
                s = "S " + Integer.toString(parameter);
                break;
            case REDUCE:
                s = "R " + Integer.toString(parameter);
                break;
            case ACCEPT:
                s = "ACC";
                break;
            case ERROR:
                s = "ERR";
                break;
        }

        return s;
    }
}
