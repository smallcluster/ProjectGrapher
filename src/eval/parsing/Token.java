package eval.parsing;

import eval.tree.NodeFactory;

class Token {
    public enum Type {
        KEYWORD,
        EOL,
        CONSTANT,
        OTHER,
        FUNCTION,
        VARIABLE,
        OPERATOR
    }
    private Type type;
    private String name;
    public Token(Type type, String name){
        this.type = type;
        this.name = name;
    }
    public boolean match(Token o){return name.equals(o.getName());}
    public Type getType() {
        return type;
    }
    public String getName() {
        return name;
    }
    @Override
    public String toString(){
        return "{ Type: "+type+", Name: "+name+" }";
    }

}
