package eval.tree.functions;

import eval.tree.Node;
import eval.tree.UnaryNode;

public class UnaryFunc extends UnaryNode {

    public UnaryFunc(String name, Node child) {
        super(child);
        this.name = name;
    }

    @Override
    public float eval(float x, float time) {
        switch (name){
            case "sin":
                return (float) Math.sin(getChild().eval(x, time));
            case "asin":
                return (float) Math.asin(getChild().eval(x, time));
            case "cos":
                return (float) Math.cos(getChild().eval(x, time));
            case "acos":
                return (float) Math.acos(getChild().eval(x, time));
            case "tan":
                return (float) Math.tan(getChild().eval(x, time));
            case "atan":
                return (float) Math.atan(getChild().eval(x, time));
            case "exp":
                return (float) Math.exp(getChild().eval(x, time));
            case "ln":
                return (float) Math.log(getChild().eval(x, time));
            case "log10":
                return (float) Math.log10(getChild().eval(x, time));
            case "abs":
                return Math.abs(getChild().eval(x, time));
            case "sqrt":
                return (float) Math.sqrt(getChild().eval(x, time));
            case "diff":
                return (getLeft().eval(x+0.001f, time)-getLeft().eval(x, time))/0.001f;
        }
        return Float.NaN;
    }

    @Override
    public String toString() {
        return name+"("+getChild()+")";
    }
}
