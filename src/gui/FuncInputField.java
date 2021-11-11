package gui;

import eval.Evaluator;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionListener;

public class FuncInputField extends JPanel {

    private Evaluator evaluator;
    private final JTextField inputField;
    private final JTextField funcName;
    private final JLabel errorLog;
    private final Graph2DPanel graph2DPanel;
    private final TreePanel treePanel;

    private Color color = Color.red;
    private final JButton colorButton;

    private final JButton saveButton;
    private FunctionList functionList;

    public FuncInputField(Graph2DPanel graph2DPanel, Evaluator evaluator, String expText, TreePanel treePanel, FunctionList functionList){
        this.evaluator = evaluator;
        this.graph2DPanel = graph2DPanel;
        this.treePanel = treePanel;
        this.functionList = functionList;

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

        funcName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                String name = funcName.getText().trim();

                if(name.isEmpty()){
                    funcName.setBackground(Color.red);
                    saveButton.setVisible(false);
                    return;
                }
                funcName.setBackground(Color.white);

                evalFunction();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                String name = funcName.getText().trim();

                if(name.isEmpty()){
                    funcName.setBackground(Color.red);
                    saveButton.setVisible(false);
                    return;
                }
                funcName.setBackground(Color.white);
                evalFunction();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                String name = funcName.getText().trim();

                if(name.isEmpty()){
                    funcName.setBackground(Color.red);
                    saveButton.setVisible(false);
                    return;
                }
                funcName.setBackground(Color.white);
                evalFunction();
            }
        });

        colorButton.addActionListener(e->{
            color = JColorChooser.showDialog(null, "Choose a color", color);
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
            funcName.setBackground(Color.white);
            functionList.add(new Function(name,  evaluator.getExpTree(), evaluator.getInput(), color));
            inputField.setText("");
            evalFunction();
        });

        // Called on button "eval" press
        evalButton.addActionListener(evalAction);
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
}
