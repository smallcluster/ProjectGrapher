package eval.parsing;

import debug.DEBUG_MODE;
import eval.automata.Automata;
import eval.automata.AutomataException;
import eval.input.Reader;

import java.util.ArrayList;

class Lexer {

    private Reader input;
    private ArrayList<Automata> machines;
    private SymbTable symbTable;

    public Lexer(Reader input, SymbTable symbTable){
        this.input = input;
        this.symbTable = symbTable;

        // Order is important !
        machines = new ArrayList<>();
        machines.add(new EOLAutomata());
        machines.add(new NumberAutomata());
        machines.add(new KeywordAutomata());
        machines.add(new CompAutomata());
        machines.add(new OpAutomata());
        machines.add(new OtherAutomata());
    }

    public int getNextEntryPos() throws LexerException {

        // Removes spaces
        int c = -1;
        while ((c = input.readChar()) == ' ');
        if(c!=-1)
            input.setPos(input.getPos()-1);


        // Try to get a token by cycling through each state machine
        for (Automata a: machines) {
            try {
                Entry t = (Entry) a.eval(input);
                if(t != null)
                    return symbTable.addEntry(t);
            } catch (AutomataException e) {
                if(DEBUG_MODE.ENABLED)
                    e.printStackTrace();
                throw new LexerException("SYNTAX ERROR"+e.getMessage());
            }
        }
        throw new LexerException("UNKNOWN SEQUENCE");
    }
}
