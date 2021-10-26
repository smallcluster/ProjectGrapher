package eval.automata;

class StateException extends Exception{
    public StateException(String message){
        super(message);
    }
    public StateException(String message, Throwable cause){
        super(message, cause);
    }
}
