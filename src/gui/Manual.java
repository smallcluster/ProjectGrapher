package gui;

import javax.swing.*;
import java.awt.*;

import static javax.swing.SwingUtilities.invokeLater;

public class Manual extends JDialog {
    public Manual(Component parent){

        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.weightx = 1;
        gc.gridx = 0;
        gc.gridy = GridBagConstraints.RELATIVE;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(5,5,5,5);
        JLabel manualLabel = new JLabel("Manual", JLabel.CENTER);
        manualLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(manualLabel, gc);

        JTabbedPane sections = new JTabbedPane();
        gc.fill = GridBagConstraints.BOTH;
        gc.weighty = 1;
        add(sections, gc);

        // ------------------------------------- Expressions -------------------------------------
        JTextPane textFormulas = new JTextPane();
        textFormulas.setContentType("text/html");
        textFormulas.setEditable(false);
        textFormulas.setFont(new Font("Arial", Font.PLAIN, 16));
        textFormulas.setText("<html>" +
                "<h1> Expressions </h1>" +
                " <p>Expressions needs to be typed in the large text input field at the application's bottom." +
                "You will find here the complete list of supported functions and operators. <br/>" +
                "Here we will denote 'EXPR' as an expression, ex: sin(x*(x^2)). Any EXPR can be composed " +
                "to form new EXPR ex: (EXPR) is an EXPR. <br/> " +
                "<font color='red'> An EXPR in a boolean test will only yield FALSE if it evaluate as 0, otherwise it will always be TRUE.</font> <br/>" +
                "<font color='red'> A boolean test is an EXPR that return 0 for FALSE and 1 for TRUE.</font></p>" +

                "<h2> Constants </h2>"+
                "<ul>" +
                "<li>exp(1): e</li>"+
                "<li>Pi: PI</li>"+
                "<li>elapsed time: TIME<br/>" +
                "<font color='green'>Use it to make an animated graph ex: sin(x+TIME)</font></li>"+
                "</ul>"+

                "<h2> Variables </h2>"+
                "<ul>" +
                "<li>x</li>"+
                "</ul>"+

                "<h2> Operators </h2>" +
                "<ul>" +
                    "<li> usual arithmetic: + - * / ^ %</li>"+
                    "<li> boolean logic: &amp&amp || &lt &gt &lt= &gt= != == ! <br/>" +
                    "<font color='green'>In this order: 'and, or, less, greater, less or equal, greater or equal, not equal, equal, inverse boolean' </font> </li>" +
                    "<li>conditional operator (ternary): EXPR ? EXPR_TRUE : EXPR_FALSE <br/>" +
                "<font color='green'>'if EXPR is TRUE then evaluate EXPR_TRUE else evaluate EXPR_FALSE'</font></li>"+
                "</ul>"+

                "<h2> Functions </h2>"+
                "<h3> No arguments </h3>"+
                "<ul>"+
                    "<li>random() <br/>" +
                    "<font color='green'> Random float number between 0 and 1</font></li>"+
                "</ul>"+
                "<h3> One argument </h3>"+
                "<ul>"+
                "<li>trigonometric functions: sin(EXPR) cos(EXPR) tan(EXPR) asin(EXPR) acos(EXPR) atan(EXPR)</li>"+
                "<li>square root: sqrt(EXPR)</li>"+
                "<li>absolute value: abs(EXPR) or |EXPR|</li>"+
                "<li>logs: ln(EXP) log10(EXPR)</li>"+
                "<li>exponential: exp(EXPR)</li>"+
                "<li>numerical derivative: diff(EXPR)</li>"+
                "</ul>"+
                "<h3> Two arguments </h3>"+
                "<ul>"+
                "<li>minimum and maximum: min(EXPR,EXPR) max(EXPR,EXPR)</li>"+
                "</ul>"+
                "</html>");
        JScrollPane expScroller = new JScrollPane(textFormulas);
        invokeLater(()->expScroller.getVerticalScrollBar().setValue(0));
        sections.add("Expressions", expScroller);

        // ------------------------------------- 2D plotting Controls -------------------------------------
        JTextPane plotControls = new JTextPane();
        plotControls.setContentType("text/html");
        plotControls.setFont(new Font("Arial", Font.PLAIN, 16));
        plotControls.setEditable(false);
        plotControls.setText("<html>" +
                "<h1> 2D Navigation </h1>" +
                " <p><font color='red'>'2D Plotting'</font> and <font color='red'>'Expression tree'</font> share the " +
                "same mouse controls:</p>" +
                "<ul>"+
                    "<li>zoom in/out: mouse wheel <br/>" +
                    "<font color='green'> Zoom relative to the mouse position in the 2d view.</font></li>"+
                    "<li>pan view: mouse left/right/middle <br/>" +
                    "<font color='green'> Left/right/middle click and drag on empty space to move the view.</font></li>"+
                "</ul>"+
                "<h2>'2D Plotting' specific</h2>"+
                "<p>Axis can be clicked and dragged (both axis at the same time or individually) to change their displayed unit size " +
                "(act as a zoom per axis)</p>"+
                "<h2>'Expression Tree' specific</h2>"+
                "<p>Nodes can be clicked and dragged around to adjust the tree, right-clicking or dragging a node with the right mouse button" +
                " will pin it in place, while a left drag won't. A pined node can be unpinned by left-clicking on it. <br/> " +
                "The <font color='red'>root</font> node can't be moved.</p>"+
                "</html>");
        JScrollPane plotControlsScroller = new JScrollPane(plotControls);
        invokeLater(()->plotControlsScroller.getVerticalScrollBar().setValue(0));
        sections.add("2D navigation", plotControlsScroller);

        // ------------------------------------- Settings options -------------------------------------
        JTextPane settingsPane = new JTextPane();
        settingsPane.setContentType("text/html");
        settingsPane.setFont(new Font("Arial", Font.PLAIN, 16));
        settingsPane.setEditable(false);
        settingsPane.setText("<html>" +
                "<h1> Settings and files </h1>" +
                "<p>Application's settings are stored in 'ProjectGrapherSettings.ini' which is automatically created in the" +
                " application working folder. Those settings are loaded at startup.</p>"+
                "<h1> File format </h1>"+
                "<p>Files are stored in an ini file (human-readable file format), some category with proper key=value are necessary like '[controls]' and '[inputField]'. <br/>" +
                "Each saved function has its own category, and they are optional.</p>" +
                "</html>");
        JScrollPane settingsScroller = new JScrollPane(settingsPane);
        invokeLater(()->settingsScroller.getVerticalScrollBar().setValue(0));
        sections.add("Settings and files", settingsScroller);






        setPreferredSize(new Dimension(720, 640));

        pack();
        setLocationRelativeTo(parent);
        setModal(true);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }
}
