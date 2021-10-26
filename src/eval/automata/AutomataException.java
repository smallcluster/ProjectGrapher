package eval.automata;

public class AutomataException extends Exception{
    public AutomataException(String message){
        super(message);
    }
    public AutomataException(String message, Throwable cause){
        super(message, cause);

    }
}
