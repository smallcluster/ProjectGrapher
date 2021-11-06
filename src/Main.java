import eval.Evaluator;
import gui.*;
import gui.TreePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;


// 1h30
public class Main extends JFrame {


    public Main() {
        setLayout(new BorderLayout());
        JTabbedPane activities = new JTabbedPane();
        add(activities, BorderLayout.CENTER);

        Evaluator evaluator = new Evaluator();

        // ------------ GRAPH TAB ----------------------------
        JPanel graphActivity2D = new JPanel();
        graphActivity2D.setLayout(new BorderLayout());
        // Graph view
        Graph2DPanel graph2DPanel = new Graph2DPanel(1280, 720, evaluator);
        graphActivity2D.add(graph2DPanel, BorderLayout.CENTER);
        // Graph controls
        GraphControl graphControl = new GraphControl(graph2DPanel);
        graphActivity2D.add(graphControl, BorderLayout.WEST);
        activities.addTab("2D Graph", graphActivity2D);
        graph2DPanel.restart(); // at first we are on the graph2DPanel

        // -------------- TREE TAB ---------------------------
        JPanel TreeActivity2D = new JPanel();
        TreeActivity2D.setLayout(new BorderLayout());
        // Tree view
        TreePanel treePanel = new TreePanel(evaluator);
        TreeActivity2D.add(treePanel, BorderLayout.CENTER);
        // Tree controls
        TreeControl treeControl = new TreeControl(treePanel);
        TreeActivity2D.add(treeControl, BorderLayout.WEST);
        activities.addTab("Tree view", TreeActivity2D);

        // Testing only
        //treePanel.restart();

        // ------------------ FUNC INPUT --------------------------------
        String input = "exp((sin(cos(x+TIME )^3)+sin(10*x)*0.1+sin(x/4)*2+cos(10+25*x)*0.05)*( |x| % 4 < 2  ? sin(-TIME)/2 : cos(TIME)/2))";
        // Current evaluated function input and error info
        FuncInputField funcInputField = new FuncInputField(graph2DPanel, evaluator, input, treePanel);
        add(funcInputField, BorderLayout.SOUTH);

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        activities.addChangeListener(e->{
            // we go to the tree tab
            if(activities.getSelectedIndex() == 1){
                graph2DPanel.stop();
                treePanel.restart();
            }
            // we go to the graph tab
            else {
                treePanel.stop();
                graph2DPanel.restart();
            }
        });
    }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings","on");
        System.setProperty("swing.aatext", "true");
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {}
        }

        Main app = new Main();
    }
}
