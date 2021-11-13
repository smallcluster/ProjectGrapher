package gui;

import eval.Evaluator;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionListener;

import static javax.swing.SwingUtilities.invokeLater;

public class FuncInputField extends JPanel {

    private final Evaluator evaluator;
    private final JTextField inputField;
    private final JTextField funcName;
    private final JLabel errorLog;
    private final Graph2DPanel graph2DPanel;
    private final TreePanel treePanel;

    private Color color = Color.red;
    private final JButton colorButton;

    private final JButton saveButton;

    public FuncInputField(Graph2DPanel graph2DPanel, Evaluator evaluator, String expText, TreePanel treePanel, FunctionList functionList){
        this.evaluator = evaluator;
        this.graph2DPanel = graph2DPanel;
        this.treePanel = treePanel;

        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;

        colorButton = new JButton(" ");
        colorButton.setBackground(color);
        add(colorButton, gc);



        Font inputFont = new Font("Arial", Font.BOLD, 18);

        funcName = new JTextField("F");
        funcName.setFont(inputFont);
        gc.gridx = 1;
        gc.weightx = 0.25;
        add(funcName, gc);

        JLabel funcLabel = new JLabel("=");
        funcLabel.setFont(inputFont);
        gc.gridx = 2;
        gc.weightx = 0;
        add(funcLabel, gc);

        inputField = new JTextField(expText);
        inputField.setFont(inputFont);
        gc.gridx = 3;
        gc.weightx = 1;
        add(inputField, gc);
        errorLog = new JLabel();
        errorLog.setFont(new Font("Arial", Font.BOLD, 12));
        gc.weightx = 1;
        gc.gridx = 3;
        gc.gridy = 1;
        add(errorLog, gc);
        JButton evalButton = new JButton("eval");
        evalButton.setFont(inputFont);
        gc.weightx = 0;
        gc.gridx = 4;
        gc.gridy = 0;
        add(evalButton, gc);

        saveButton = new JButton("save");
        saveButton.setVisible(false);
        saveButton.setFont(inputFont);
        gc.weightx = 0;
        gc.gridx = 5;
        gc.gridy = 0;
        add(saveButton, gc);


        // Parse provided input to the current evaluator or raise error
        ActionListener evalAction = e -> evalFunction();


        // Called on "Enter" key press
        inputField.addActionListener(evalAction);
        // Called when typing
        inputField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                evalFunction();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                evalFunction();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                evalFunction();
            }
        });

        funcName.addActionListener(e-> updateFunctionName());

        funcName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateFunctionName();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateFunctionName();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateFunctionName();
            }
        });

        colorButton.addActionListener(e->{
            color = JColorChooser.showDialog(getRootPane(), "Choose a color", color);
            color = new Color(color.getRed(), color.getGreen(), color.getBlue());
            colorButton.setBackground(color);
            Function f = graph2DPanel.getCurrentFunction();
            if(f != null){
                f.setColor(color);
            }
        });

        saveButton.addActionListener(e->{
            String name = funcName.getText().trim();

            if(name.isEmpty()){
                funcName.setBackground(Color.red);
                return;
            }
            // Ask user to override function if they have the same name
            funcName.setBackground(Color.white);
            Function f = new Function(name,  evaluator.getExpTree(), evaluator.getInput(), color);

            int i = functionList.findFunction(f);
            if( i != -1){
                int ask = JOptionPane.showConfirmDialog(getRootPane(), "Function \""+f.getName()+"\" already exist, override it ?", "Override function", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if(ask == JOptionPane.YES_OPTION){
                    functionList.setFunctionAt(i, f);
                    inputField.setText("");
                    evalFunction();
                }
            } else {
                functionList.add(f);
                inputField.setText("");
                evalFunction();
            }
        });

        // Called on button "eval" press
        evalButton.addActionListener(evalAction);
    }

    public String getText(){
        return inputField.getText();
    }

    public Color getColor(){
        return color;
    }

    public void updateFunctionName(){
        invokeLater(()->{
            String oldName = funcName.getText();
            String name = funcName.getText().replace(' ', '_');
            if(!name.equals(oldName))
                funcName.setText(name);
            if(name.isEmpty()){
                funcName.setBackground(Color.red);
                saveButton.setVisible(false);
                return;
            }
            funcName.setBackground(Color.white);
            evalFunction();
        });
    }



    public void evalFunction(){
        String name = funcName.getText().trim();

        if(name.isEmpty()){
            funcName.setBackground(Color.red);
            saveButton.setVisible(false);
            return;
        }
        funcName.setBackground(Color.white);

        int n = evaluator.parse(inputField.getText());

        // Exp is Empty
        if(n == Evaluator.EMPTY){
            inputField.setBackground(Color.white);
            inputField.setForeground(Color.black);
            errorLog.setVisible(false);
            treePanel.buildParticleSim();
            graph2DPanel.setCurrentFunction(null);
            saveButton.setVisible(false);
        }
        // Exp is OK
        else if(n == Evaluator.OK){
            inputField.setBackground(Color.white);
            inputField.setForeground(Color.black);
            errorLog.setVisible(false);
            treePanel.buildParticleSim();
            graph2DPanel.setCurrentFunction(new Function(funcName.getText(), evaluator.getExpTree(), evaluator.getInput(), color));
            saveButton.setVisible(true);
        }
        // Exp is invalid
        else {
            String txt = "<html>Expression error: char '" +
                        "<font color=\"red\">" +
                        inputField.getText().charAt(n) +
                        "</font>' at " +
                        (n + 1) +
                        "</html>";
            inputField.setBackground(Color.red);
            inputField.setForeground(Color.white);
            errorLog.setText(txt);
            errorLog.setVisible(true);
            graph2DPanel.setCurrentFunction(null);
            saveButton.setVisible(false);
        }
    }

    public void setFunction(Function f){
        color = f.getColor();
        colorButton.setBackground(color);
        inputField.setText(f.getExp());
        funcName.setText(f.getName());
        evalFunction();
    }

    public void setColor(Color color) {
        this.color = color;
        colorButton.setBackground(color);
        evalFunction();
    }

    public String getFunctionName(){
        return funcName.getText();
    }

    public void setFunctionName(String name){
        funcName.setText(name.trim().replace(' ', '_'));
        evalFunction();
    }

    public void setFunctionExp(String exp) {
        inputField.setText(exp);
        evalFunction();
    }
}
