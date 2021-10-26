package eval.tree.operator;

import eval.tree.Node;
import eval.tree.UnaryNode;

public class UnaryOp extends UnaryNode {

    public UnaryOp(String name, Node child) {
        super(child);
        this.name = name;
    }

    @Override
    public float eval(float x, float time) {
        switch (name){
            case "-":
                return (-1)*getChild().eval(x, time);
            case "!":
                return getChild().eval(x, time) == 0 ? 1 : 0;
        }
        return Float.NaN;
    }

    @Override
    public String toString() {
        return name +"("+getChild()+")";
    }
}
