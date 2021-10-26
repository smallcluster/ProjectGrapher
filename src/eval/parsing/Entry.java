package eval.parsing;
import eval.tree.NodeFactory;

public class Entry {

    private Token token;
    private NodeFactory factory;

    public Entry(Token token, NodeFactory factory){
        this.token = token;
        this.factory = factory;
    }

    public Entry(Token.Type type, String name, NodeFactory factory){
        this.token = new Token(type, name);
        this.factory = factory;
    }

    public Token getToken(){return token;}
    public NodeFactory getFactory(){return factory;}
    @Override
    public String toString() {
        return "( " + token + ", " + factory+" )";
    }
    public boolean match(Entry e){
        return token.match(e.getToken());
    }
}
