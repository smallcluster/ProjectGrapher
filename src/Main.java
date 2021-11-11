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


        // Function List
        FunctionList functionList = new FunctionList();
        JScrollPane funcListScroller = new JScrollPane(functionList);
        funcListScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        //add(funcListScroller, BorderLayout.EAST);

        Evaluator evaluator = new Evaluator();

        // ------------ GRAPH TAB ----------------------------
        JPanel graphActivity2D = new JPanel();
        graphActivity2D.setLayout(new BorderLayout());
        // Graph view
        Graph2DPanel graph2DPanel = new Graph2DPanel(1280, 720, functionList);
        graphActivity2D.add(graph2DPanel, BorderLayout.CENTER);

        // Graph controls
        GraphControl graphControl = new GraphControl(graph2DPanel);

        JPanel graphControlAndFunclist = new JPanel();
        graphControlAndFunclist.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.NORTH;
        gc.fill = GridBagConstraints.HORIZONTAL;
        graphControlAndFunclist.add(graphControl, gc);
        gc.gridy = 1;
        gc.insets = new Insets(10, 0, 0, 0);
        JLabel label = new JLabel("Functions:", JLabel.CENTER);
        graphControlAndFunclist.add(label, gc);
        gc.insets = new Insets(0, 0, 0, 0);
        gc.gridy = 2;
        gc.weighty = 1;
        graphControlAndFunclist.add(funcListScroller, gc);
        JScrollPane graphControlScroller = new JScrollPane(graphControlAndFunclist);
        graphControlScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


        graphActivity2D.add(graphControlScroller, BorderLayout.WEST);

        activities.addTab("2D Plotting", graphActivity2D);

        // -------------- TREE TAB ---------------------------
        JPanel TreeActivity2D = new JPanel();
        TreeActivity2D.setLayout(new BorderLayout());
        // Tree view
        TreePanel treePanel = new TreePanel(evaluator);
        TreeActivity2D.add(treePanel, BorderLayout.CENTER);
        // Tree controls
        TreeControl treeControl = new TreeControl(treePanel);

        JScrollPane treeControlScroller = new JScrollPane(treeControl);
        treeControlScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        TreeActivity2D.add(treeControlScroller, BorderLayout.WEST);
        activities.addTab("Expression tree", TreeActivity2D);


        // ------------------ FUNC INPUT --------------------------------
        String input = "exp((sin(cos(x+TIME )^3)+sin(10*x)*0.1+sin(x/4)*2+cos(10+25*x)*0.05)*( |x| % 4 < 2  ? sin(-TIME)/2 : cos(TIME)/2))";
        // Current evaluated function input and error info
        FuncInputField funcInputField = new FuncInputField(graph2DPanel, evaluator, input, treePanel, functionList);
        add(funcInputField, BorderLayout.SOUTH);


        // Bind funcInputField and functionList REQUIRED !!!!!!
        functionList.setFuncInputField(funcInputField);

        graph2DPanel.restart(); // at first, we are on the graph2DPanel

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

        new Main();
    }
}
