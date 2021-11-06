package gui;

import javax.swing.*;
import java.awt.*;

public class GraphControl extends JPanel {
    Graph2DPanel graph2DPanel;

    public GraphControl(Graph2DPanel graph2DPanel){
        this.graph2DPanel = graph2DPanel;

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        add(container);

//        InputField xmin = new InputField("x min", "-10");
//        container.add(xmin);
//        InputField xmax = new InputField("x max", "10");
//        container.add(xmax);
//        InputField ymin = new InputField("y min", "-10");
//        container.add(ymin);
//        InputField ymax = new InputField("y max", "10");
//        container.add(ymax);

        JButton recenter = new JButton("Recenter");
        container.add(recenter);
        recenter.addActionListener(e -> graph2DPanel.recenter());


    }
}
