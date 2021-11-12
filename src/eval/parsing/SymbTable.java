package eval.parsing;

import eval.tree.varconst.ConstFactory;
import eval.tree.NodeFactory;
import eval.tree.varconst.VarFactory;
import eval.tree.functions.FuncFactory;

import java.util.ArrayList;

class SymbTable {
    ArrayList<Entry> entries;

    public SymbTable() {
        entries = new ArrayList<>();

        // Reserved keywords
        addEntry(Token.Type.EOL, "EOL", null);

        // CTE
        addEntry(Token.Type.CONSTANT, "e", new ConstFactory("e", 2.71828182846f));
        addEntry(Token.Type.CONSTANT, "PI", new ConstFactory("PI", 3.14159265359f));

        // VARIABLES
        addEntry(Token.Type.VARIABLE, "x", new VarFactory("x"));
        addEntry(Token.Type.VARIABLE, "TIME", new VarFactory("TIME"));

        // FUNCTIONS
        addEntry(Token.Type.FUNCTION, "sin", new FuncFactory("sin", 1));
        addEntry(Token.Type.FUNCTION, "cos", new FuncFactory("cos", 1));
        addEntry(Token.Type.FUNCTION, "tan", new FuncFactory("tan", 1));
        addEntry(Token.Type.FUNCTION, "asin", new FuncFactory("asin", 1));
        addEntry(Token.Type.FUNCTION, "acos", new FuncFactory("acos", 1));
        addEntry(Token.Type.FUNCTION, "atan", new FuncFactory("atan", 1));
        addEntry(Token.Type.FUNCTION, "sqrt", new FuncFactory("sqrt", 1));
        addEntry(Token.Type.FUNCTION, "abs", new FuncFactory("abs", 1));
        addEntry(Token.Type.FUNCTION, "ln", new FuncFactory("ln", 1));
        addEntry(Token.Type.FUNCTION, "log10", new FuncFactory("log10", 1));
        addEntry(Token.Type.FUNCTION, "min", new FuncFactory("min", 2));
        addEntry(Token.Type.FUNCTION, "max", new FuncFactory("max", 2));
        addEntry(Token.Type.FUNCTION, "exp", new FuncFactory("exp", 1));
        addEntry(Token.Type.FUNCTION, "diff", new FuncFactory("diff", 1));
        addEntry(Token.Type.FUNCTION, "random", new FuncFactory("random", 0));

    }

    public Entry getEntryAt(int i){
        return entries.get(i);
    }

    public int getEntryPos(Entry o){
        for (Entry t: entries) {
            if(t.match(o))
                return entries.indexOf(t);
        }
        return -1;
    }

    public int addEntry(Token token, NodeFactory factory){
        return addEntry(new Entry(token, factory));
    }

    public int addEntry(Token.Type type, String name, NodeFactory factory){
        return addEntry(new Entry(new Token(type, name), factory));
    }

    public int addEntry(Entry t){
        int pos = getEntryPos(t);
        if( pos == -1){
            entries.add(t);
            pos = entries.size()-1;
        }
        return pos;
    }

    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder("{\n");
        for (Entry t: entries) {
            txt.append(t).append(",\n");
        }
        txt.append("}");
        return txt.toString();
    }
}
