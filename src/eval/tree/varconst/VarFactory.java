package eval.tree.varconst;

import eval.tree.Node;
import eval.tree.NodeFactory;

public class VarFactory extends NodeFactory {

    public VarFactory(String name) {
        super(name);
    }

    @Override
    public Node createNew() {
        return new Var(name);
    }
}
