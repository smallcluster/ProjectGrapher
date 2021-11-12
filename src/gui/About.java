package gui;

import eval.tree.Node;
import eval.tree.functions.UnaryFunc;
import eval.tree.varconst.Var;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class About extends JDialog {
    public About(Component parent){

        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        gc.gridx = 0;
        gc.gridy = GridBagConstraints.RELATIVE;
        gc.insets = new Insets(10, 10, 10, 10);

        Font fontName = new Font("Arial", Font.BOLD, 32);
        JLabel name = new JLabel("Project Grapher v1.5", JLabel.CENTER);
        name.setFont(fontName);
        gc.gridwidth = 2;
        add(name, gc);

        JLabel githLabel = new JLabel("Github: ");
        gc.weightx = 0;
        gc.gridwidth = 1;
        add(githLabel, gc);

        JTextField githubLink = new JTextField("https://github.com/smallcluster/ProjectGrapher");
        githubLink.setEditable(false);
        gc.weightx = 1;
        gc.gridx = 1;
        add(githubLink, gc);


        pack();
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setModal(true);
        setVisible(true);
    }
}
