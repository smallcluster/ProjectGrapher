package eval.parsing;

import eval.automata.State;
import eval.tree.operator.OpFactory;

class OpAutomata extends EntryAutomata {

    public OpAutomata(){
        State q1 = new State();
        current.addTransition(q1, "+-*/%^");
        current.addTransition(fail, "+-*/%^", false);
        q1.addTransition(end);
    }

    @Override
    protected Entry returnOnSuccess() {
        return new Entry(Token.Type.OPERATOR, buffer.toString(), new OpFactory(buffer.toString()));
    }
}
