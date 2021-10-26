package eval.parsing;

import eval.automata.AutomataException;
import eval.automata.State;
import eval.tree.varconst.ConstFactory;

class NumberAutomata extends EntryAutomata {

    public NumberAutomata(){
        State q1 = new State();
        State q2 = new State();
        State q3 = new State();
        current.addTransition(fail, "[:digit:].", false);
        current.addTransition(q1, "[:digit:]");
        current.addTransition(q2, ".");
        q1.addTransition(q1, "[:digit:]");
        q1.addTransition(q3, ".");
        q1.addTransition(end, ".[:digit:][:letter:]", false);
        q3.addTransition(q3, "[:digit:]");
        q3.addTransition(end, "[:letter:][:digit:].", false);
        q2.addTransition(q3, "[:digit:]");
    }

    @Override
    protected AutomataException exception() {
        return new AutomataException("Invalid number : "+buffer);
    }

    @Override
    protected Entry returnOnSuccess() {
        String val = buffer.toString();
        return new Entry(Token.Type.CONSTANT, val , new ConstFactory(val, Float.parseFloat(val)));
    }

}
