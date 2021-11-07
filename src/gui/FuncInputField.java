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
    private final JLabel errorLog;
    private final Graph2DPanel graph2DPanel;
    private final TreePanel treePanel;

    public FuncInputField(Graph2DPanel graph2DPanel, Evaluator evaluator, String expText, TreePanel treePanel){
        this.evaluator = evaluator;
        this.graph2DPanel = graph2DPanel;
        this.treePanel = treePanel;

        setLayout(new BorderLayout());

        Font inputFont = new Font("Arial", Font.BOLD, 18);
        JButton evalButton = new JButton("eval");
        evalButton.setFont(inputFont);
        add(evalButton, BorderLayout.EAST);


        JPanel inputLogContainer = new JPanel();
        inputLogContainer.setLayout(new BorderLayout());
        add(inputLogContainer, BorderLayout.CENTER);

        JLabel funcLabel = new JLabel("f(x)=");
        funcLabel.setFont(inputFont);
        inputLogContainer.add(funcLabel, BorderLayout.WEST);
        inputField = new JTextField(expText);
        inputField.setFont(inputFont);
        inputLogContainer.add(inputField, BorderLayout.CENTER);
        errorLog = new JLabel();
        errorLog.setFont(new Font("Arial", Font.BOLD, 12));
        inputLogContainer.add(errorLog, BorderLayout.SOUTH);

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

        // Called on button "eval" press
        evalButton.addActionListener(evalAction);
    }

    public void evalFunction(){
        int n = evaluator.parse(inputField.getText());
        if (n != -1) {
            String txt = "<html>Expression error : char '" +
                    "<font color=\"red\">" +
                    inputField.getText().charAt(n) +
                    "</font>' at " +
                    (n + 1) +
                    "</html>";

            inputField.setBackground(Color.red);
            inputField.setForeground(Color.white);

            errorLog.setText(txt);
            errorLog.setVisible(true);
        } else {
            inputField.setBackground(Color.white);
            inputField.setForeground(Color.black);
            errorLog.setVisible(false);
            graph2DPanel.repaint();
            treePanel.buildParticleSim();
        }
    }



}
