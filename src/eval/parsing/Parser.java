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
    private final Token lt = new Token(Token.Type.OPERATOR, "<");
    private final Token le = new Token(Token.Type.OPERATOR, "<=");
    private final Token gt = new Token(Token.Type.OPERATOR, ">");
    private final Token ge = new Token(Token.Type.OPERATOR, ">=");
    private final Token eq = new Token(Token.Type.OPERATOR, "==");
    private final Token neq = new Token(Token.Type.OPERATOR, "!=");
    private final Token neg = new Token(Token.Type.OPERATOR, "!");
    private final Token tern = new Token(Token.Type.OTHER, "?");
    private final Token testsep = new Token(Token.Type.OTHER, ":");
    private final Token and = new Token(Token.Type.OPERATOR, "&&");
    private final Token or = new Token(Token.Type.OPERATOR, "||");
    private final Token mult = new Token(Token.Type.OPERATOR, "*");
    private final Token EOL = new Token(Token.Type.EOL, "EOL");
    private final Token minus = new Token(Token.Type.OPERATOR, "-");
    private final Token add = new Token(Token.Type.OPERATOR, "+");
    private final Token div = new Token(Token.Type.OPERATOR, "/");
    private final Token pow = new Token(Token.Type.OPERATOR, "^");
    private final Token mod = new Token(Token.Type.OPERATOR, "%");
    private final Token bar = new Token(Token.Type.OTHER, "|");
    private final Token openGroup = new Token(Token.Type.OTHER, "(");
    private final Token closeGroup = new Token(Token.Type.OTHER, ")");
    private final Token coma = new Token(Token.Type.OTHER, ",");


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
        return L();
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
            return new FuncFactory("abs", 1).createNew(h);
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
            FuncFactory funcFactory = (FuncFactory) lookAheadFactory;
            match(lookAhead);
            match(openGroup);
            // FUNCTION ()
            if(lookAhead.match(closeGroup)){
                match(closeGroup);
                if(funcFactory.getArgsCount() != 0)
                    throw new ParserException("Function "+funcFactory.getName()+" expected 0 args, got "+funcFactory.getArgsCount());
                return funcFactory.createNew();
            }
            Node h = H();
            // FUNCTION ( H , H)
            if(lookAhead.match(coma)){
                match(lookAhead);
                Node h2 = H();
                match(closeGroup);
                if(funcFactory.getArgsCount() != 2)
                    throw new ParserException("Function "+funcFactory.getName()+" expected 2 args, got "+funcFactory.getArgsCount());
                return funcFactory.createNew(h, h2);
            }
            // FUNCTION ( H )
            match(closeGroup);
            if(funcFactory.getArgsCount() != 1)
                throw new ParserException("Function "+funcFactory.getName()+" expected 1 args, got "+funcFactory.getArgsCount());
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
