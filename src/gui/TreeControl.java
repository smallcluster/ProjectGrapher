package gui;

import javax.swing.*;

public class TreeControl extends JPanel {
    TreePanel treePanel;

    public TreeControl(TreePanel treePanel){
        this.treePanel = treePanel;

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        add(container);

        JButton recenter = new JButton("Recenter");
        container.add(recenter);
        recenter.addActionListener(e -> treePanel.recenter());

        JButton reunfold = new JButton("Re-unfold");
        container.add(reunfold);
        reunfold.addActionListener(e -> treePanel.recrush());


    }
}
