package eval.tree.operator;

import eval.tree.Node;
import eval.tree.NodeFactory;

public class OpFactory  extends NodeFactory {

    public OpFactory(String name){
        super(name);
    }

    @Override
    public Node createNew(Node left, Node right) {
        return new BinaryOp(name, left, right);
    }

    @Override
    public Node createNew(Node child) {
        return new UnaryOp(name, child);
    }

}
