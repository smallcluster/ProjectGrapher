package eval.parsing;

import eval.automata.State;
import eval.tree.operator.OpFactory;

public class CompAutomata extends EntryAutomata {

    public CompAutomata(){
        State q1 = new State();
        State q2 = new State();
        State q3 = new State();
        State q4 = new State();
        State q5 = new State();

        current.addTransition(q1, "><!");
        current.addTransition(q2, "=");
        current.addTransition(q4, "&");
        q4.addTransition(q3, "&");
        q4.addTransition(fail, "&", false);
        current.addTransition(q5, "|");
        q5.addTransition(q3, "|");
        q5.addTransition(fail, "|", false);
        current.addTransition(fail, "!=><&", false);

        q1.addTransition(end, "=", false);
        q1.addTransition(q3, "=");

        q2.addTransition(q3, "=");

        q3.addTransition(end);
    }

    @Override
    protected Entry returnOnSuccess() {
        return new Entry(Token.Type.OPERATOR, buffer.toString(), new OpFactory(buffer.toString()));
    }
}
