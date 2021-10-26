package eval.parsing;

import eval.automata.Automata;

abstract class EntryAutomata extends Automata<Entry> {
    @Override
    public Entry returnOnFail() {
        return null;
    }
}
