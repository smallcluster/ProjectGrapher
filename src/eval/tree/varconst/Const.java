package eval.tree.varconst;

import eval.tree.Node;

public class Const extends Node {
    float val;

    public Const(float val) {
        name = Float.toString(val);
        this.val = val;
    }
    public Const(String name, float val) {
        this.name = name;
        this.val = val;
    }

    @Override
    public float eval(float x, float time) {
        return val;
    }

    @Override
    public String toString() {
        return name;
    }
}
