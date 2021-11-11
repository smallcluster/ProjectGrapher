package eval.tree;


public abstract class NodeFactory {

    protected String name;

    public String getName(){
        return name;
    }

    public NodeFactory(String name){
        this.name = name;
    }

    public Node createNew(Node left, Node right) {return null;}
    public Node createNew(Node child){return null;};
    public Node createNew(){return null;};

    @Override
    public String toString() {
        return "F -> "+name;
    }
}
