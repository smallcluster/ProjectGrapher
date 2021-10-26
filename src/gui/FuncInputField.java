package gui;

import eval.Evaluator;
import gui.treeview.TreeView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class FuncInputField extends JPanel {

    public FuncInputField(Graph2DPanel graph2DPanel, Evaluator evaluator, String expText, TreeView treeView){

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
        JTextField inputField = new JTextField(expText);
        inputField.setFont(inputFont);
        inputLogContainer.add(inputField, BorderLayout.CENTER);
        JLabel errorLog = new JLabel();
        errorLog.setFont(new Font("Arial", Font.BOLD, 12));
        inputLogContainer.add(errorLog, BorderLayout.SOUTH);

        // Parse provided input to the current evaluator or raise error
        ActionListener evalAction = e -> {
            int n = evaluator.parse(inputField.getText());
            if (n != -1) {
                StringBuilder txt = new StringBuilder();
                txt.append("<html>Expression error : char '");
                txt.append("<font color=\"red\">");
                txt.append(inputField.getText().charAt(n));
                txt.append("</font>' at ");
                txt.append(n + 1);
                txt.append("</html>");

                inputField.setBackground(Color.red);
                inputField.setForeground(Color.white);

                errorLog.setText(txt.toString());
                errorLog.setVisible(true);
            } else {
                inputField.setBackground(Color.white);
                inputField.setForeground(Color.black);
                errorLog.setVisible(false);
                graph2DPanel.repaint();
                treeView.buildParticleSim();
            }
        };

        // Called on "Enter" key press
        inputField.addActionListener(evalAction);
        // Called on button "eval" press
        evalButton.addActionListener(evalAction);
    }
}
