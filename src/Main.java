import eval.Evaluator;
import gui.*;
import gui.TreePanel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.*;


class Settings extends JDialog {

    private final JSlider fpsCap;
    private  final JCheckBox antiAliasing;

    public boolean getAntialias(){
        return antiAliasing.isSelected();
    }
    public int getFpsCap(){
        return fpsCap.getValue();
    }

    public Settings(Main parent, int fps, boolean antialias){

        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        gc.fill = GridBagConstraints.HORIZONTAL;

        setTitle("Settings");

        JLabel fpsCapLabel = new JLabel("FPS cap: "+fps);
        add(fpsCapLabel, gc);

        gc.gridx = 1;
        gc.weightx = 1;
        fpsCap = new JSlider();
        fpsCap.setMinimum(5);
        fpsCap.setMaximum(144);
        fpsCap.setValue(fps);
        add(fpsCap, gc);
        fpsCap.addChangeListener(e-> fpsCapLabel.setText("FPS cap: "+fpsCap.getValue()));

        gc.gridx = 0;
        gc.weightx = 0;
        gc.gridy = 1;
        antiAliasing = new JCheckBox("Antialiasing");
        antiAliasing.setSelected(antialias);
        add(antiAliasing, gc);

        gc.gridx = 1;
        gc.weightx = 1;
        gc.insets = new Insets(0, 10, 0, 0);
        JLabel warning = new JLabel("<html> <font color='red'>HUGE PERFORMANCE IMPACT</font></html>");
        add(warning, gc);

        JButton applyButton = new JButton("Apply");
        gc.gridy = 2;
        gc.gridx = 0;
        gc.gridwidth = 2;
        gc.insets = new Insets(0,0,0,0);
        add(applyButton, gc);
        applyButton.addActionListener(e->{
            parent.setAntialias(getAntialias());
            parent.setFpscap(getFpsCap());
            parent.saveSettings();
            parent.applySettings();
        });

        pack();
        setLocationRelativeTo(parent);


        setModal(true);
        setVisible(true);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}


public class Main extends JFrame {

    private final Graph2DPanel graph2DPanel;
    private final FuncInputField funcInputField;
    private final GraphControl graphControl;
    private final FunctionList functionList;
    private final TreePanel treePanel;

    // ---------------- Settings options ini ---------------------------
    private final String settingsIniName = "ProjectGrapherSettings.ini";

    private int fpscap = 60;
    private boolean antialias = false;

    public void setFpscap(int fpscap) {
        this.fpscap = fpscap;
    }

    public void setAntialias(boolean antialias) {
        this.antialias = antialias;
    }

    public void loadSettings(){
        HashMap<String, HashMap<String, String>> ini = parseIniFile(settingsIniName);
        if (ini == null) return;
        HashMap<String, String> vals = ini.get("settings");
        if(vals == null) return;
        fpscap = Integer.parseInt(vals.get("fpsCap"));
        antialias = Integer.parseInt(vals.get("antiAliasing")) == 1;
    }

    public void saveSettings(){
        File file = new File(settingsIniName);
        try {
            FileWriter fileWriter = new FileWriter(file);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            // Controls
            printWriter.println("[settings]");
            printWriter.println(String.format(Locale.US, "fpsCap=%d", fpscap));
            printWriter.println(String.format(Locale.US, "antiAliasing=%d", antialias ? 1 : 0));
            printWriter.close();

        } catch (IOException ignored) {}
    }

    public void applySettings(){
        graph2DPanel.setFrameCap(fpscap);
        treePanel.setFrameCap(fpscap);
        graph2DPanel.setAntialiasing(antialias);
        treePanel.setAntialiasing(antialias);
    }



    // file path
    // if empty, show a file selection dialog
    private String path = "";


    public Main() {
        setLayout(new BorderLayout());
        JTabbedPane activities = new JTabbedPane();
        add(activities, BorderLayout.CENTER);


        // Function List
        functionList = new FunctionList();
        JScrollPane funcListScroller = new JScrollPane(functionList);
        funcListScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        //add(funcListScroller, BorderLayout.EAST);

        Evaluator evaluator = new Evaluator();

        // ------------ GRAPH TAB ----------------------------
        JPanel graphActivity2D = new JPanel();
        graphActivity2D.setLayout(new BorderLayout());
        // Graph view
        graph2DPanel = new Graph2DPanel(1280, 720, functionList);
        graphActivity2D.add(graph2DPanel, BorderLayout.CENTER);

        // Graph controls
        graphControl = new GraphControl(graph2DPanel);

        JPanel graphControlAndFunclist = new JPanel();
        graphControlAndFunclist.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.NORTH;
        gc.fill = GridBagConstraints.HORIZONTAL;
        graphControlAndFunclist.add(graphControl, gc);
        gc.gridy = 1;
        gc.insets = new Insets(5, 0, 5, 0);
        JLabel label = new JLabel("Functions:", JLabel.CENTER);
        graphControlAndFunclist.add(new JSeparator(), gc);
        gc.gridy = 2;
        graphControlAndFunclist.add(label, gc);
        gc.insets = new Insets(0, 0, 0, 0);
        gc.gridy = 3;
        gc.weighty = 1;
        graphControlAndFunclist.add(funcListScroller, gc);
        JScrollPane graphControlScroller = new JScrollPane(graphControlAndFunclist);
        //graphControlScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


        graphActivity2D.add(graphControlScroller, BorderLayout.WEST);

        activities.addTab("2D Plotting", graphActivity2D);

        // -------------- TREE TAB ---------------------------
        JPanel TreeActivity2D = new JPanel();
        TreeActivity2D.setLayout(new BorderLayout());
        // Tree view
        treePanel = new TreePanel(evaluator);
        TreeActivity2D.add(treePanel, BorderLayout.CENTER);
        // Tree controls
        TreeControl treeControl = new TreeControl(treePanel);

        JScrollPane treeControlScroller = new JScrollPane(treeControl);
        treeControlScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        TreeActivity2D.add(treeControlScroller, BorderLayout.WEST);
        activities.addTab("Expression tree", TreeActivity2D);


        // ------------------ FUNC INPUT --------------------------------
        String input = "";
        // Current evaluated function input and error info
        funcInputField = new FuncInputField(graph2DPanel, evaluator, input, treePanel, functionList);
        add(funcInputField, BorderLayout.SOUTH);


        // Bind funcInputField and functionList REQUIRED !!!!!!
        functionList.setFuncInputField(funcInputField);

        graph2DPanel.restart(); // at first, we are on the graph2DPanel


        // ----------------------------- MENU BAR --------------------------------
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // File menu
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        JMenuItem newFileItem = new JMenuItem("New File");
        fileMenu.add(newFileItem);
        KeyStroke ctrlN = KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        newFileItem.setAccelerator(ctrlN);

        fileMenu.add(new JSeparator());
        JMenuItem saveFileItem = new JMenuItem("Save File ...");
        fileMenu.add(saveFileItem);


        KeyStroke ctrlS = KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        saveFileItem.setAccelerator(ctrlS);

        saveFileItem.addActionListener(e -> {
            if (path.isEmpty()) {
                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle("Choose a file");
                fc.setFileFilter(new FileNameExtensionFilter("config", "ini"));
                int result = fc.showSaveDialog(getRootPane());
                if (result != JFileChooser.APPROVE_OPTION) return;

                File f = fc.getSelectedFile();
                path = f.getAbsolutePath();
                if (!path.endsWith(".ini")) path += ".ini";
            }
            save(path);
        });


        JMenuItem saveFileAsItem = new JMenuItem("Save File As ...");
        fileMenu.add(saveFileAsItem);

        KeyStroke ctrlA = KeyStroke.getKeyStroke(KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        saveFileAsItem.setAccelerator(ctrlA);

        saveFileAsItem.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Choose a file");
            fc.setFileFilter(new FileNameExtensionFilter("config", "ini"));
            int result = fc.showSaveDialog(getRootPane());
            if (result != JFileChooser.APPROVE_OPTION) return;

            File f = fc.getSelectedFile();
            path = f.getAbsolutePath();
            if (!path.endsWith(".ini")) path += ".ini";
            save(path);
        });

        JMenuItem openFileItem = new JMenuItem("Open File ...");
        fileMenu.add(openFileItem);

        KeyStroke ctrlO = KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        openFileItem.setAccelerator(ctrlO);

        openFileItem.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Choose a file");
            fc.setFileFilter(new FileNameExtensionFilter("config", "ini"));
            int result = fc.showSaveDialog(getRootPane());
            if (result != JFileChooser.APPROVE_OPTION) return;

            File f = fc.getSelectedFile();
            path = f.getAbsolutePath();
            if (!path.endsWith(".ini")) path += ".ini";
            load(path);
        });
        fileMenu.add(new JSeparator());
        JMenuItem quitItem = new JMenuItem("Quit");
        fileMenu.add(quitItem);
        quitItem.addActionListener(e -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));

        KeyStroke ctrlX = KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        quitItem.setAccelerator(ctrlX);

        // Edit menu
        JMenu editMenu = new JMenu("Edit");
        menuBar.add(editMenu);
        JMenuItem settingsItem = new JMenuItem("Settings");
        editMenu.add(settingsItem);
        settingsItem.addActionListener(e-> new Settings(this,fpscap, antialias));

        // View menu
        JMenu viewMenu = new JMenu("View");
        menuBar.add(viewMenu);
        JCheckBoxMenuItem gridItem = new JCheckBoxMenuItem("Display Grid");
        gridItem.addActionListener(e -> graph2DPanel.setShowGrid(gridItem.isSelected()));
        gridItem.setSelected(true);
        viewMenu.add(gridItem);
        JCheckBoxMenuItem fpsItem = new JCheckBoxMenuItem("Display FPS");
        fpsItem.setSelected(true);
        fpsItem.addActionListener(e -> {
            graph2DPanel.setShowFps(fpsItem.isSelected());
            treePanel.setShowFps(fpsItem.isSelected());
        });
        viewMenu.add(fpsItem);
        JCheckBoxMenuItem controlItem = new JCheckBoxMenuItem("Display Controls");
        controlItem.addActionListener(e -> {
            graphControlScroller.setVisible(controlItem.isSelected());
            treeControlScroller.setVisible(controlItem.isSelected());
        });
        controlItem.setSelected(true);
        viewMenu.add(controlItem);

        newFileItem.addActionListener(e -> {
            int ask = JOptionPane.showConfirmDialog(getRootPane(), "Save on creating new file ?", "New File", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if(ask != JOptionPane.YES_OPTION && ask != JOptionPane.NO_OPTION) return;

            if (ask == JOptionPane.YES_OPTION) {

                if (path.isEmpty()) {
                    JFileChooser fc = new JFileChooser();
                    fc.setDialogTitle("Choose a file");
                    fc.setFileFilter(new FileNameExtensionFilter("config", "ini"));
                    int result = fc.showSaveDialog(getRootPane());
                    if(result != JFileChooser.APPROVE_OPTION) return;
                    File f = fc.getSelectedFile();
                    path = f.getAbsolutePath();
                    if (!path.endsWith(".ini")) path += ".ini";
                    save(path);
                } else
                    save(path);
            }
            graph2DPanel.setPixelsPerUnitX(64.0f);
            graph2DPanel.setPixelsPerUnitY(64.0f);
            graph2DPanel.setOffsetX(0);
            graph2DPanel.setOffsetY(0);
            graphControl.setAutoStep(true);
            gridItem.setSelected(true);
            graph2DPanel.setShowGrid(true);
            functionList.clear();
            funcInputField.setFunction(new Function("F", null, "", Color.red));

        });

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);
        JMenuItem aboutItem = new JMenuItem("About");
        helpMenu.add(aboutItem);
        aboutItem.addActionListener(e->new About(this));
        JMenuItem manualItem = new JMenuItem("Manual");
        helpMenu.add(manualItem);

        manualItem.addActionListener(e->new Manual(this));

        // Load settings
        loadSettings();
        applySettings();


        pack();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Ask to save before exiting
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int ask = JOptionPane.showConfirmDialog(getRootPane(), "Save on exit ?", "Exit", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (ask == JOptionPane.YES_OPTION) {
                    if (path.isEmpty()) {
                        JFileChooser fc = new JFileChooser();
                        fc.setDialogTitle("Choose a file");
                        fc.setFileFilter(new FileNameExtensionFilter("config", "ini"));
                        int result = fc.showSaveDialog(getRootPane());
                        if (result == JFileChooser.APPROVE_OPTION) {
                            File f = fc.getSelectedFile();
                            path = f.getAbsolutePath();
                            if (!path.endsWith(".ini")) path += ".ini";
                            save(path);
                            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                            dispose();
                        }
                    } else {
                        save(path);
                        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        dispose();
                    }
                } else if(ask == JOptionPane.NO_OPTION) {
                    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    dispose();
                }
            }
        });
        setVisible(true);

        activities.addChangeListener(e -> {
            // we go to the tree tab
            if (activities.getSelectedIndex() == 1) {
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

    public void save(String path) {

        File file = new File(path);
        try {
            FileWriter fileWriter = new FileWriter(file);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            // Controls
            printWriter.println("[controls]");
            printWriter.println(String.format(Locale.US, "xmin=%f", graph2DPanel.getWorldX(0)));
            printWriter.println(String.format(Locale.US, "xmax=%f", graph2DPanel.getWorldX(graph2DPanel.getWidth())));
            printWriter.println(String.format(Locale.US, "ymin=%f", graph2DPanel.getWorldY(graph2DPanel.getHeight())));
            printWriter.println(String.format(Locale.US, "ymax=%f", graph2DPanel.getWorldY(0)));
            printWriter.println(String.format(Locale.US, "autoStep=%d", graph2DPanel.isAutoStep() ? 1 : 0));
            printWriter.println(String.format(Locale.US, "step=%f", graph2DPanel.getStep()));

            // Function Input field
            String inputFuncName = funcInputField.getFunctionName().trim();
            printWriter.println("\n[inputField]");
            printWriter.println(String.format(Locale.US, "name=%s", inputFuncName.isEmpty() ? "F" : inputFuncName));
            printWriter.println(String.format(Locale.US, "exp=%s", funcInputField.getText().trim()));
            Color inputColor = funcInputField.getColor();
            printWriter.println(String.format(Locale.US, "color=%s", String.format("#%02X%02X%02X", inputColor.getRed(), inputColor.getGreen(), inputColor.getBlue())));

            // Functions
            ArrayList<Function> functions = functionList.getFunctions();
            for (int i = 0; i < functions.size(); i++) {
                Function f = functions.get(i);
                printWriter.println("\n[function" + i + "]");
                printWriter.println(String.format(Locale.US, "name=%s", f.getName()));
                printWriter.println(String.format(Locale.US, "exp=%s", f.getExp()));
                printWriter.println(String.format(Locale.US, "visible=%d", f.isVisible() ? 1 : 0));
                Color color = f.getColor();
                printWriter.println(String.format(Locale.US, "color=%s", String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue())));
            }
            printWriter.close();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(getRootPane(), "Can't save to file: " + path, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public HashMap<String, HashMap<String, String>> parseIniFile(String path){
        // ini file parsing
        HashMap<String, HashMap<String, String>> iniFile = new HashMap<>();
        File file = new File(path);
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader br = new BufferedReader(fileReader);
            String line;
            HashMap<String, String> options = null;
            String category;
            while ((line = br.readLine()) != null) {
                // it's a category
                if (line.startsWith("[")) {
                    category = line.replace("[", "");
                    category = category.replace("]", "");
                    options = new HashMap<>();
                    iniFile.put(category.trim(), options);
                    continue;
                }
                // it's an option
                int pos = line.indexOf('=');
                if (pos != -1) {
                    String key = line.substring(0, pos).trim();
                    String val = line.substring(pos + 1).trim();
                    // no category => ignore values
                    if (options == null)
                        continue;
                    options.put(key, val);
                }
            }
            br.close();
            // end of ini parsing

        } catch (IOException e) {
            return null;
        }
        return iniFile;
    }

    public void load(String path) {

        HashMap<String, HashMap<String, String>> iniFile = parseIniFile(path);
        if(iniFile == null){
            JOptionPane.showMessageDialog(getRootPane(), "Can't open file: " + path, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // get values
        Evaluator ev = new Evaluator();
        functionList.clear();
        for (String key : iniFile.keySet()) {
            HashMap<String, String> params = iniFile.get(key);

            if (key.equals("controls")) {
                float xmin = Float.parseFloat(params.get("xmin"));
                float xmax = Float.parseFloat(params.get("xmax"));
                float ymin = Float.parseFloat(params.get("ymin"));
                float ymax = Float.parseFloat(params.get("ymax"));
                float step = Float.parseFloat(params.get("step"));
                boolean autoStep = Integer.parseInt(params.get("autoStep")) == 1;

                graphControl.setRegion(xmin, xmax, ymin, ymax);
                graph2DPanel.setRegion(xmin, xmax, ymin, ymax);
                graphControl.setStep(step);
                graph2DPanel.setStep(step);
                graphControl.setAutoStep(autoStep);
                graph2DPanel.setAutoStep(autoStep);

            } else if (key.equals("inputField")) {
                String name = params.get("name");
                String exp = params.get("exp");
                Color color = Color.decode(params.get("color"));
                funcInputField.setColor(color);
                funcInputField.setFunctionExp(exp);
                functionList.setName(name);
            } else if (key.startsWith("function")) {
                System.out.println(key);
                String name = params.get("name");
                String exp = params.get("exp");
                boolean visible = Integer.parseInt(params.get("visible")) == 1;
                Color color = Color.decode(params.get("color"));
                ev.parse(exp);
                Function f = new Function(name, ev.getExpTree(), exp, color);
                f.setVisible(visible);
                functionList.add(f);
            }
        }
    }


    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
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
            } catch (Exception ex) {
            }
        }

        new Main();
    }
}
