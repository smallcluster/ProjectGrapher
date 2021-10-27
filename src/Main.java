import eval.Evaluator;
import gui.*;
import gui.treeview.TreeView;

import javax.swing.*;
import java.awt.*;


// 1h30
public class Main extends JFrame {


    public Main() {
        setLayout(new BorderLayout());
        JTabbedPane activities = new JTabbedPane();
        add(activities, BorderLayout.CENTER);


        Evaluator evaluator = new Evaluator();
        TreeView treeView = new TreeView(evaluator);

        JPanel activity2D = new JPanel();
        activity2D.setLayout(new BorderLayout());
        // Graph view
        Graph2DPanel graph2DPanel = new Graph2DPanel(1280, 720, evaluator);
        activity2D.add(graph2DPanel, BorderLayout.CENTER);
        // Graph controls
        GraphControl graphControl = new GraphControl(graph2DPanel);
        activity2D.add(graphControl, BorderLayout.WEST);

        activities.addTab("2D Graph", activity2D);
        activities.addTab("Tree view", treeView);

        String input = "exp((sin(cos(x+TIME )^3)+sin(10*x)*0.1+sin(x/4)*2+cos(10+25*x)*0.05)*( |x| % 4 < 2  ? sin(-TIME)/2 : cos(TIME)/2))";

        // Current evaluated function input and error info
        FuncInputField funcInputField = new FuncInputField(graph2DPanel, evaluator, input, treeView);
        add(funcInputField, BorderLayout.SOUTH);

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                System.out.println(info.getName());
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
