package eval.tree;

public abstract class Node {

    private Node left, right;

    protected String name= "";

    public Node(){
        left = null;
        right = null;
    }

    public Node(Node left, Node right) {
        this.left = left;
        this.right = right;
    }

    public String getName() { return name;}

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public abstract float eval(float x, float time);
    public abstract String toString();
}
