package eval.parsing;

import eval.automata.State;
import eval.tree.operator.OpFactory;

class EOLAutomata extends EntryAutomata {

    public EOLAutomata(){
        State q1 = new State();
        current.addTransition(q1, "[:EOL:]");
        current.addTransition(fail, "[:EOL:]", false);
        q1.addTransition(end);
    }

    @Override
    protected Entry returnOnSuccess() {
        return new Entry(Token.Type.EOL, "EOL", null);
    }
}
