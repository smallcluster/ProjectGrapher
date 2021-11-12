package gui;

import eval.tree.functions.UnaryFunc;
import eval.tree.varconst.Var;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import static javax.swing.SwingUtilities.invokeLater;


class FunctionControls extends JPanel {

    private Function function;

    private final JButton remove;
    private final JButton selector;
    private final JButton color;
    private final JCheckBox visibility;

    public FunctionControls(Function f) {
        function = f;
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        // Visibility checkbox
        visibility = new JCheckBox();
        add(visibility, gc);
        visibility.addActionListener(e -> function.setVisible(visibility.isSelected()));
        visibility.setSelected(true);
        // Color picker
        color = new JButton(" ");
        color.setBackground(f.getColor());
        gc.gridx = 1;
        add(color, gc);
        color.addActionListener(e -> {
            Color c = JColorChooser.showDialog(null, "Choose a color", function.getColor());
            function.setColor(c);
            color.setBackground(c);
        });
        // Function selection
        selector = new JButton(f.getName());
        gc.gridx = 2;
        gc.weightx = 1;
        add(selector, gc);
        // Remove function
        remove = new JButton("X");
        gc.gridx = 3;
        gc.weightx = 0;
        add(remove, gc);
    }

    public void addRemoveActionListener(ActionListener e) {
        remove.addActionListener(e);
    }

    public void addSelectActionListener(ActionListener e) {
        selector.addActionListener(e);
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function f) {
        function = f;
        function.setVisible(visibility.isSelected());
        color.setBackground(f.getColor());
    }

    public void setFunctionVisible(boolean visible) {
        function.setVisible(visible);
        visibility.setSelected(visible);
    }
}


public class FunctionList extends JPanel {

    private final GridBagConstraints gc;
    private final ArrayList<FunctionControls> funcContList;
    private FuncInputField funcInputField = null;

    public void setFuncInputField(FuncInputField funcInputField) {
        this.funcInputField = funcInputField;
    }

    public FunctionList() {
        funcContList = new ArrayList<>();
        setLayout(new GridBagLayout());
        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.NORTH;
        gc.gridy = GridBagConstraints.RELATIVE;
        gc.insets = new Insets(5, 0, 0, 0);
        add(new Function("sin", new UnaryFunc("sin", new Var("x")), "sin(x)", Color.GREEN));
        add(new Function("cos", new UnaryFunc("cos", new Var("x")), "cos(x)", Color.blue));
    }

    public int findFunction(Function f) {
        for (int i = 0; i < funcContList.size(); i++) {
            if (funcContList.get(i).getFunction().getName().equals(f.getName())) {
                return i;
            }
        }
        return -1;
    }

    public void setFunctionAt(int i, Function f) {
        funcContList.get(i).setFunction(f);
        revalidate();
        repaint();
    }

    public void add(Function f) {

        FunctionControls fc = new FunctionControls(f);
        fc.addSelectActionListener(e -> funcInputField.setFunction(fc.getFunction()));
        fc.addRemoveActionListener(e -> invokeLater(() -> {
            remove(fc);
            funcContList.remove(fc);
            revalidate();
            repaint();
        }));

        add(fc, gc);
        funcContList.add(fc);
        revalidate();
        repaint();
    }

    public ArrayList<Function> getFunctions() {
        ArrayList<Function> list = new ArrayList<>();
        synchronized (funcContList) {
            for (FunctionControls fc : funcContList) {
                list.add(fc.getFunction());
            }
        }
        return list;
    }

    public void clear() {
        removeAll();
        funcContList.clear();
    }

}
