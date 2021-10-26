package eval.input;

public class StringReader extends Reader {
    private String input;

    public StringReader(String input){
        super();
        this.input = input;
    }

    @Override
    public boolean endOfStream() {
        return pos == input.length();
    }

    @Override
    protected int readFromStream() {
        return input.charAt(pos++);
    }
}
