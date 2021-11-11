package eval.tree.functions;

import eval.tree.Node;
import eval.tree.NodeFactory;

public class FuncFactory extends NodeFactory {

    private int argsCount;

    public int getArgsCount(){
        return argsCount;
    }

    public FuncFactory(String name, int argsCount) {
        super(name);
        this.argsCount = argsCount;
    }

    @Override
    public Node createNew(Node left, Node right) {
        return new BinaryFunc(name, left, right);
    }

    @Override
    public Node createNew(Node child) {
        return new UnaryFunc(name, child);
    }

    @Override
    public Node createNew() {
        return new LeafFunc(name);
    }
}
