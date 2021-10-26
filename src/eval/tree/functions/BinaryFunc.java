package eval.tree.functions;

import eval.tree.Node;

public class BinaryFunc extends Node {

    public BinaryFunc(String name, Node left, Node right) {
        super(left, right);
        this.name = name;
    }

    @Override
    public float eval(float x, float time) {
        switch (name){
            case "min":
                return (float) Math.min(getLeft().eval(x, time), getRight().eval(x, time));
            case "max":
                return (float) Math.max(getLeft().eval(x, time), getRight().eval(x, time));
        }
        return Float.NaN;
    }

    @Override
    public String toString() {
        return name+"("+getLeft()+", "+getRight()+")";
    }
}
