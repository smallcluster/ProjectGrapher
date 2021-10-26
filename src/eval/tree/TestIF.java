package eval.tree;

import eval.tree.Node;

public class TestIF extends Node {
    Node cond;
    public TestIF(Node cond, Node yes, Node no) {
        super(yes, no);// left = yes, right = no
        this.cond = cond;
        name = "IF";
    }

    @Override
    public float eval(float x, float time) {
        return cond.eval(x, time) == 0 ? getRight().eval(x, time) : getLeft().eval(x, time);
    }

    public Node getCond(){return cond;}

    @Override
    public String toString() {
        return "("+ cond + " ? " + getLeft() + " : " + getRight()+")";
    }


}
