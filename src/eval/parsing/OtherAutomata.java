package eval.parsing;

import eval.automata.State;
import eval.tree.operator.OpFactory;

class OtherAutomata extends EntryAutomata {

    public OtherAutomata(){
        State q1 = new State();
        current.addTransition(q1, "(,)|?:");
        q1.addTransition(end);
    }

    @Override
    protected Entry returnOnSuccess() {
        return new Entry(Token.Type.OTHER, buffer.toString(), null);
    }
}
