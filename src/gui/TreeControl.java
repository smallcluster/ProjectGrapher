package gui;

import javax.swing.*;
import java.awt.*;

public class TreeControl extends JPanel {
    TreePanel treePanel;

    public TreeControl(TreePanel treePanel){
        this.treePanel = treePanel;

        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.NORTH;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;


        JButton reunfold = new JButton("Re-unfold");
        add(reunfold, gc);
        reunfold.addActionListener(e -> treePanel.recrush());

        JButton recenter = new JButton("Recenter view");
        gc.gridy = 1;
        gc.weighty = 1;
        add(recenter, gc);
        recenter.addActionListener(e -> treePanel.recenter());




    }
}
