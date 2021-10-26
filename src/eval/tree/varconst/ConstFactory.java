package eval.tree.varconst;

import eval.tree.Node;
import eval.tree.NodeFactory;

public class ConstFactory extends NodeFactory {

    private float value;

    public ConstFactory(String name, float value) {
        super(name);
        this.value = value;
    }

    @Override
    public Node createNew(Node left, Node right) {
        return new Const(name, value);
    }

    @Override
    public Node createNew(Node child) {
        return new Const(name, value);
    }

    @Override
    public Node createNew() {
        return new Const(name, value);
    }

}
