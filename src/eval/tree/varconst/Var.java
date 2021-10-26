package eval.tree.varconst;

import eval.tree.Node;

public class Var extends Node {

    public Var(String name){
        super();
        this.name = name;
    }

    @Override
    public float eval(float x, float time) {
        switch (name){
            case "x":
                return x;
            case "TIME":
                return time;
        }
        return Float.NaN;
    }

    @Override
    public String toString() {
        return name;
    }
}
