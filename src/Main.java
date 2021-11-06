import eval.Evaluator;
import gui.*;
import gui.TreePanel;

import javax.swing.*;
import java.awt.*;


// 1h30
public class Main extends JFrame {


    public Main() {
        setLayout(new BorderLayout());
        JTabbedPane activities = new JTabbedPane();
        add(activities, BorderLayout.CENTER);

        Evaluator evaluator = new Evaluator();

        // ------------ GRAPH TAB ----------------------------
        JPanel GraphActivity2D = new JPanel();
        GraphActivity2D.setLayout(new BorderLayout());
        // Graph view
        Graph2DPanel graph2DPanel = new Graph2DPanel(1280, 720, evaluator);
        GraphActivity2D.add(graph2DPanel, BorderLayout.CENTER);
        // Graph controls
        GraphControl graphControl = new GraphControl(graph2DPanel);
        GraphActivity2D.add(graphControl, BorderLayout.WEST);
        activities.addTab("2D Graph", GraphActivity2D);

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

        // ------------------ FUNC INPUT --------------------------------
        String input = "exp((sin(cos(x+TIME )^3)+sin(10*x)*0.1+sin(x/4)*2+cos(10+25*x)*0.05)*( |x| % 4 < 2  ? sin(-TIME)/2 : cos(TIME)/2))";
        // Current evaluated function input and error info
        FuncInputField funcInputField = new FuncInputField(graph2DPanel, evaluator, input, treePanel);
        add(funcInputField, BorderLayout.SOUTH);

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
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
