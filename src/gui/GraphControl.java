package gui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GraphControl extends JPanel {
    Graph2DPanel graph2DPanel;

    InputField xmin, xmax, ymin, ymax, step;
    JCheckBox autoStep;

    public GraphControl(Graph2DPanel graph2DPanel) {

        setLayout(new GridBagLayout());

        this.graph2DPanel = graph2DPanel;

        xmin = new InputField("x min", "-10");
        add(0, xmin, 0);
        xmax = new InputField("x max", "10");
        add(1, xmax, 0);
        ymin = new InputField("y min", "-5");
        add(2, ymin, 0);
        ymax = new InputField("y max", "5");
        add(3, ymax, 0);
        step = new InputField("step", "0.01");
        add(4, step, 0);
        autoStep = new JCheckBox("auto step");
        autoStep.setSelected(true);
        add(5, autoStep, 0);
        JButton colorChoser = new JButton("Set color");
        add(6, colorChoser, 0);
        JButton recenter = new JButton("Recenter view");
        add(7, recenter, 0);
        JCheckBox showGrid = new JCheckBox("Show grid");
        showGrid.setSelected(true);
        add(8, showGrid, 1);

        colorChoser.addActionListener(e -> setGraphColor());
        recenter.addActionListener(e -> graph2DPanel.recenter());
        autoStep.addActionListener(e -> graph2DPanel.setAutoStep(autoStep.isSelected()));
        showGrid.addActionListener(e-> graph2DPanel.setShowGrid(showGrid.isSelected()));


        step.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                setGraphStep();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                setGraphStep();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                setGraphStep();
            }
        });
        step.addActionListener(e -> setGraphStep());

        DocumentListener d = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                setGraphRegion();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                setGraphRegion();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                setGraphRegion();
            }
        };

        ActionListener regionAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setGraphRegion();
            }
        };

        xmin.addDocumentListener(d);
        ymin.addDocumentListener(d);
        xmax.addDocumentListener(d);
        ymax.addDocumentListener(d);

        xmin.addActionListener(regionAction);
        ymin.addActionListener(regionAction);
        xmax.addActionListener(regionAction);
        ymax.addActionListener(regionAction);



        graph2DPanel.setGraphControl(this);
    }

    private void add(int y, Component c, int weight) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = y;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        gc.weighty = weight;
        gc.anchor = GridBagConstraints.NORTH;
        add(c, gc);
    }

    private void setGraphColor(){
        Color c = JColorChooser.showDialog(null, "Choose a color", Color.red);
        graph2DPanel.setGraphColor(c);
    }

    private float getFloatFromInput(InputField input) {
        float val;
        try {
            val = Float.parseFloat(input.getInputText());
            input.clearError();
        } catch (Exception e) {
            val = Float.NaN;
            input.showError();
        }
        return val;
    }

    private void setGraphStep() {
        if (autoStep.isSelected())
            return;
        float val = getFloatFromInput(step);

        if (Float.isNaN(val) || val <= 0)
            step.showError();
        else {
            step.clearError();
            graph2DPanel.setStep(val);
        }

    }

    private void setGraphRegion() {
        float minx = getFloatFromInput(xmin);
        float maxx = getFloatFromInput(xmax);
        float miny = getFloatFromInput(ymin);
        float maxy = getFloatFromInput(ymax);
        if (Float.isNaN(minx) || Float.isNaN(maxx) || Float.isNaN(miny) || Float.isNaN(maxy))
            return;
        if (minx >= maxx) {
            xmin.showError();
            return;
        }
        xmin.clearError();
        if (miny >= maxy) {
            ymin.showError();
            return;
        }
        ymin.clearError();
        // All good
        graph2DPanel.setRegion(minx, maxx, miny, maxy);
    }

    public void setRegion(float minx, float maxx, float miny, float maxy) {
        xmin.setInputText(Float.toString(minx));
        xmax.setInputText(Float.toString(maxx));
        ymin.setInputText(Float.toString(miny));
        ymax.setInputText(Float.toString(maxy));
        xmin.clearError();
        xmax.clearError();
        ymax.clearError();
        ymin.clearError();
    }

    public void setStep(float val) {
        step.setInputText(Float.toString(val));
        step.clearError();
    }


}
