package gui;

import eval.tree.Node;
import eval.tree.varconst.Var;

import java.awt.*;

public class Function {
    private String exp;

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp.replaceAll(" ", "");
    }

    private Node function;
    private Color color;
    private String name;
    private boolean visible;

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Function(){
        function = new Var("x");
        color = Color.red;
        name = "f";
        visible = true;
        exp = "x";
    }

    public String getName(){
        return name;
    }

    public void setName(String val){
        name = val;
    }

    @Override
    public String toString(){
        return getName()+"="+function.toString();
    }

    public Function(String name, Node function, String exp, Color color){
        this.name = name;
        this.function = function;
        this.color = color;
        visible = true;
        this.exp = exp.replaceAll(" ", "");
    }

    public float eval(float x, float time){
        return function.eval(x,time);
    }

    public Color getColor(){
        return color;
    }

    public void setColor(Color c){
        this.color =c;
    }

    private Node getFunction(){
        return function;
    }

    private void setFunction(Node f){
        function = f;
    }
}
