package eval.tree.functions;

import eval.tree.Node;

public class LeafFunc extends Node {

    public LeafFunc(String name){
        this.name = name;
    }

    @Override
    public float eval(float x, float time) {
        switch (name){
            case "random":
                return (float) Math.random();
        }
        return Float.NaN;
    }

    @Override
    public String toString() {
        return name+"()";
    }
}
