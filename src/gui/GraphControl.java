package gui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionListener;

public class GraphControl extends JPanel {
    private final Graph2DPanel graph2DPanel;

    private final InputField xmin, xmax, ymin, ymax, step;
    private final JCheckBox autoStep;

    public void setAutoStep(boolean b){
        autoStep.setSelected(b);
        graph2DPanel.setAutoStep(b);
    }

    public GraphControl(Graph2DPanel graph2DPanel) {
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        gc.anchor = GridBagConstraints.NORTH;

        this.graph2DPanel = graph2DPanel;

        gc.weighty = 0;
        gc.gridy = GridBagConstraints.RELATIVE;
        xmin = new InputField("x min", "-10");
        add(xmin, gc);
        xmax = new InputField("x max", "10");
        add(xmax, gc);
        ymin = new InputField("y min", "-5");
        add(ymin, gc);
        ymax = new InputField("y max", "5");
        add(ymax, gc);
        step = new InputField("step", "0.01");
        add(step, gc);
        autoStep = new JCheckBox("auto step");
        autoStep.setSelected(true);
        add(autoStep, gc);
        JButton recenter = new JButton("Recenter view");
        add(recenter, gc);

        recenter.addActionListener(e -> graph2DPanel.recenter());
        autoStep.addActionListener(e -> graph2DPanel.setAutoStep(autoStep.isSelected()));


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

        ActionListener regionAction = e -> setGraphRegion();

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
