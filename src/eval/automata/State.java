package eval.automata;

import java.util.ArrayList;

public class State {

    public static enum Type {
        ACCEPT,
        FAIL,
        NORMAL
    }

    private Type type;
    ArrayList<Transition> transitions = new ArrayList<>();

    public State(Type type){
        this.type = type;
    }
    public State(){
        this.type = Type.NORMAL;
    }

    public void addTransition(Transition t){
        transitions.add(t);
    }
    public void addTransition(State s){
        transitions.add(new Transition(s));
    }
    public void addTransition(State s, String cond){
        transitions.add(new Transition(s, cond));
    }
    public void addTransition(State s, String cond, boolean match){
        transitions.add(new Transition(s, cond, match));
    }

    public Type getType() {return type;}

    public State transit(int c) throws StateException {
        for (Transition t: transitions) {
            if (t.eval(c))
                return t.getNextState();
        }
        throw new StateException("No transition found");
    }
}
