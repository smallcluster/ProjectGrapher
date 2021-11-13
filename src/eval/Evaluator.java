package eval;

import eval.input.Reader;
import eval.input.StringReader;
import eval.tree.Node;
import eval.parsing.Parser;
import eval.parsing.ParserException;

import debug.DEBUG_MODE;

public class Evaluator {

    public static int OK = -1;
    public static int EMPTY = -2;

    private Node exp;
    Parser parser;
    String input;

    public Evaluator(){
        parser = new Parser();
        this.exp = null;
        this.input = "";
    }

    public Evaluator(String input){
        parser = new Parser();
        this.exp = null;
        this.input = input.replaceAll(" ", "");
        parse(this.input);
    }

    public int parse(String input) {
        this.input = input.replaceAll(" ", "");
        Reader reader = new StringReader(this.input);
        try {
            exp = parser.parse(reader);
            return exp == null ? EMPTY : OK; // empty is an invalid exp
        } catch (ParserException e) {
            exp = null;
            if(DEBUG_MODE.ENABLED)
                e.printStackTrace();
            return reader.getPos() > 0 ? reader.getPos()-1 : reader.getPos();
        }
    }

    public Node getExpTree(){return exp;}

    public boolean isExpValid(){
        return exp != null;
    }

    public float eval(float x, float time) {
        return exp.eval(x, time);
    }

    public String getInput(){return input;}

    @Override
    public String toString() {
        return exp.toString();
    }
}
