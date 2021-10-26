package eval.parsing;

import eval.automata.AutomataException;
import eval.automata.State;
import eval.tree.operator.OpFactory;

class KeywordAutomata extends EntryAutomata {

    public KeywordAutomata(){
        State q1 = new State();
        State q2 = new State();
        current.addTransition(q1, "_");
        current.addTransition(q2, "[:letter:]");
        current.addTransition(fail, "_[:letter:]", false);
        q1.addTransition(q1, "_");
        q1.addTransition(q2, "[:digit:][:letter:]");
        q2.addTransition(q2, "_[:digit:][:letter:]");
        q2.addTransition(end, "_[:digit:][:letter:]", false);
    }

    @Override
    protected AutomataException exception() {
        return new AutomataException("Invalid keyword : "+buffer);
    }

    @Override
    protected Entry returnOnSuccess() {
        return new Entry(Token.Type.KEYWORD, buffer.toString(), null);
    }
}
