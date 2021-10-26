package eval.tree.operator;

import eval.tree.Node;

public class BinaryOp extends Node {


    public BinaryOp(String name, Node left, Node right) {
        super(left, right);
        this.name = name;
    }

    @Override
    public float eval(float x, float time) {
        switch (name){
            case "+":
                return getLeft().eval(x, time) + getRight().eval(x, time);
            case "-":
                return getLeft().eval(x, time) - getRight().eval(x, time);
            case "*":
                return getLeft().eval(x, time) * getRight().eval(x, time);
            case "/":
                return getLeft().eval(x, time) / getRight().eval(x, time);
            case "^":
                return (float) Math.pow(getLeft().eval(x, time), getRight().eval(x, time));
            case "%":
                return getLeft().eval(x, time) % getRight().eval(x, time);
            case "==":
                return getLeft().eval(x, time) == getRight().eval(x, time) ? 1 : 0;
            case "<=":
                return getLeft().eval(x, time) <= getRight().eval(x, time) ? 1 : 0;
            case ">=":
                return getLeft().eval(x, time) >= getRight().eval(x, time) ? 1 : 0;
            case "<":
                return getLeft().eval(x, time) < getRight().eval(x, time) ? 1 : 0;
            case ">":
                return getLeft().eval(x, time) > getRight().eval(x, time) ? 1 : 0;
            case "!=":
                return getLeft().eval(x, time) != getRight().eval(x, time) ? 1 : 0;
            case "&&":
                return getLeft().eval(x, time) == 0 || getRight().eval(x, time) == 0 ? 0 : 1;
            case "||":
                return getLeft().eval(x, time) == 0 && getRight().eval(x, time)==0 ? 0 : 1;
        }
        return Float.NaN;
    }

    @Override
    public String toString() {
        return "("+getLeft() + name + getRight()+")";
    }
}