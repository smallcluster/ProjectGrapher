package eval.tree;

public abstract class UnaryNode extends Node {
    public UnaryNode(Node child) {
        super(child, null);
    }
    public Node getChild(){
        return getLeft();
    }
    public void setChild(Node child){
        setLeft(child);
    }
}
