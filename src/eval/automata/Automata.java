package eval.automata;

import eval.input.Reader;

public abstract class Automata<T> {

    protected State current;
    protected StringBuilder buffer = new StringBuilder();
    protected State end;
    protected State fail;
    private State init;

    public Automata(){
        current = new State();
        init = current;
        end = new State(State.Type.ACCEPT);
        fail = new State(State.Type.FAIL);
    }
    protected AutomataException exception(){
        return new AutomataException("Unknown error, buffer = "+buffer);
    }

    public T eval(Reader input) throws AutomataException {
        int prevPos = input.getPos();
        while (true) {
            int c = input.readChar();
            try {
                current = current.transit(c);
            } catch (StateException e){
                buffer.append((char) c);
                throw exception();
            }
            if(current.getType() == State.Type.ACCEPT){
                if(c != -1)
                    input.setPos(input.getPos()-1);
                T val = returnOnSuccess();
                reset();
                return val;
            } else if(current.getType() == State.Type.FAIL){
                input.setPos(prevPos);
                T val = returnOnFail();
                reset();
                return val;
            } else {
                if(c != -1 && c != ' ')
                    buffer.append((char) c);
            }
        }
    }
    private void reset(){
        current = init;
        buffer = new StringBuilder();
    }

    protected abstract T returnOnSuccess();
    protected abstract T returnOnFail();
}
