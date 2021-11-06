package eval.parsing;

import debug.DEBUG_MODE;
import eval.input.Reader;
import eval.tree.Node;
import eval.tree.NodeFactory;
import eval.tree.TestIF;
import eval.tree.functions.FuncFactory;

public class Parser {

    private SymbTable symbTable;
    private Lexer lexer;
    private Token lookAhead;
    private NodeFactory lookAheadFactory;

    // Some token
    private Token lt = new Token(Token.Type.OPERATOR, "<");
    private Token le = new Token(Token.Type.OPERATOR, "<=");
    private Token gt = new Token(Token.Type.OPERATOR, ">");
    private Token ge = new Token(Token.Type.OPERATOR, ">=");
    private Token eq = new Token(Token.Type.OPERATOR, "==");
    private Token neq = new Token(Token.Type.OPERATOR, "!=");
    private Token neg = new Token(Token.Type.OPERATOR, "!");
    private Token tern = new Token(Token.Type.OTHER, "?");
    private Token testsep = new Token(Token.Type.OTHER, ":");
    private Token and = new Token(Token.Type.OPERATOR, "&&");
    private Token or = new Token(Token.Type.OPERATOR, "||");
    private Token mult = new Token(Token.Type.OPERATOR, "*");
    private Token EOL = new Token(Token.Type.EOL, "EOL");
    private Token minus = new Token(Token.Type.OPERATOR, "-");
    private Token add = new Token(Token.Type.OPERATOR, "+");
    private Token div = new Token(Token.Type.OPERATOR, "/");
    private Token pow = new Token(Token.Type.OPERATOR, "^");
    private Token mod = new Token(Token.Type.OPERATOR, "%");
    private Token bar = new Token(Token.Type.OTHER, "|");
    private Token openGroup = new Token(Token.Type.OTHER, "(");
    private Token closeGroup = new Token(Token.Type.OTHER, ")");
    private Token coma = new Token(Token.Type.OTHER, ",");


    public Node parse(Reader input) throws ParserException {
        symbTable = new SymbTable();
        lexer = new Lexer(input, symbTable);
        try {
            Entry entry = symbTable.getEntryAt(lexer.getNextEntryPos());
            lookAhead = entry.getToken();
            lookAheadFactory = entry.getFactory();
        } catch (LexerException e) {
            if(DEBUG_MODE.ENABLED)
                e.printStackTrace();
            throw new ParserException("Can't get a new token -> "+e.getMessage());
        }
        Node l = L();
        return l;
    }

    private void match(Token t) throws ParserException {
        if(lookAhead.match(t)){
            try {
                Entry entry = symbTable.getEntryAt(lexer.getNextEntryPos());
                lookAhead = entry.getToken();
                lookAheadFactory = entry.getFactory();
            } catch (LexerException e) {
                if(DEBUG_MODE.ENABLED)
                    e.printStackTrace();
                throw new ParserException("Can't get a new token");
            }
        } else {
            throw new ParserException("Expected: "+t+", got: "+lookAhead);
        }
    }

    // L -> eol | H eol
    // Strange grammar but prevents to many closing ")" or "|"
    Node L() throws ParserException {
        if(lookAhead.match(EOL)){
            match(EOL);
            return null;
        }
        Node h = H();
        match(EOL);
        return h;
    }

    // H -> J ? H : H | J
    // Right to left
    private Node H() throws ParserException {
        Node j = J();
        if(lookAhead.match(tern)){
            match(tern);
            Node h1 = H();
            match(testsep);
            Node h2 = H();
            return new TestIF(j, h1, h2);
        }
        return j;
    }

    // J -> C { ("&&" | "||") C }
    // Left to right
    private Node J() throws ParserException {
        Node tree = C();
        while(lookAhead.match(and) || lookAhead.match(or)){
            NodeFactory factory = lookAheadFactory;
            match(lookAhead);
            Node next = C();
            tree = factory.createNew(tree, next); // stack from left to right ex 1 + 2 - 4 -> (( 1 + 2 ) - 4)
        }
        return tree;
    }

    // C -> E { ("<=" | "<" | ">=" | ">" | "==" | "!=") E }
    // Left to right
    private Node C() throws ParserException {
        Node tree = E();
        while(lookAhead.match(le) || lookAhead.match(lt) || lookAhead.match(ge) || lookAhead.match(gt) || lookAhead.match(eq) || lookAhead.match(neq)){
            NodeFactory factory = lookAheadFactory;
            match(lookAhead);
            Node next = E();
            tree = factory.createNew(tree, next); // stack from left to right ex 1 + 2 - 4 -> (( 1 + 2 ) - 4)
        }
        return tree;
    }


    // E -> T  { ("+" |"-") T }
    // Left to right
    private Node E() throws ParserException{
        Node tree = T();
        while(lookAhead.match(add) || lookAhead.match(minus)){
            NodeFactory factory = lookAheadFactory;
            match(lookAhead);
            Node next = T();
            tree = factory.createNew(tree, next); // stack from left to right ex 1 + 2 - 4 -> (( 1 + 2 ) - 4)
        }
        return tree;
    }

    // T ->  A { ("*" | "/" | "%") A }
    // Left to right
    private Node T() throws ParserException {
        Node tree = A();
        while(lookAhead.match(mult) || lookAhead.match(div) || lookAhead.match(mod)){
            NodeFactory factory = lookAheadFactory;
            match(lookAhead);
            Node next = A();
            tree = factory.createNew(tree, next); // stack from left to right ex 1 * 2 / 4 -> (( 1 * 2 ) / 4)
        }
        return tree;
    }

    // A -> !A | -A | F ^ A | F
    // Right to left
    private Node A() throws ParserException {
        // Test ! or - first
        if(lookAhead.match(neg) || lookAhead.match(minus)){
            NodeFactory factory = lookAheadFactory;
            match(lookAhead);
            Node a = A();
            return factory.createNew(a);
        }
        Node f = F();
        // F ^ T
        if(lookAhead.match(pow)){
            NodeFactory factory = lookAheadFactory;
            match(lookAhead);
            Node a = A();
            return factory.createNew(f,a);
        }
        // F
        return f;
    }

    // F -> | H |  |  ( H ) | FUNCTION () | FUNCTION ( H , H ) | FUNCTION ( H ) | CONSTANT | VARIABLE
    private Node F() throws ParserException {
        // |H|
        if(lookAhead.match(bar)){
            match(lookAhead);
            Node h = H();
            match(bar);
            return new FuncFactory("abs").createNew(h);
        }
        // ( H )
        else if(lookAhead.match(openGroup)){
            match(lookAhead);
            Node h = H();
            match(closeGroup);
            return h;
        }
        // functions
        else if(lookAhead.getType() == Token.Type.FUNCTION) {
            NodeFactory funcFactory = lookAheadFactory;
            match(lookAhead);
            match(openGroup);
            // FUNCTION ()
            if(lookAhead.match(closeGroup)){
                match(closeGroup);
                return funcFactory.createNew();
            }
            Node h = H();
            // FUNCTION ( H , H)
            if(lookAhead.match(coma)){
                match(lookAhead);
                Node h2 = H();
                match(closeGroup);
                return funcFactory.createNew(h, h2);
            }
            // FUNCTION ( H )
            match(closeGroup);
            return funcFactory.createNew(h);
        }
        // CONSTANT or VAR
        else if(lookAhead.getType() == Token.Type.CONSTANT || lookAhead.getType() == Token.Type.VARIABLE){
            NodeFactory factory = lookAheadFactory;
            match(lookAhead);
            return factory.createNew();
        }
        throw new ParserException("Expected '(', CONSTANT or VARIABLE. Got: "+lookAhead);
    }
}
