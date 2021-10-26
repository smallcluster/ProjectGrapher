package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class InputField extends JPanel {

    JLabel label;
    JTextField input;

    InputField(String name, String inputText, Font font){
        setLayout(new FlowLayout(FlowLayout.TRAILING));
        label = new JLabel(name);
        input = new JTextField(inputText);
        label.setFont(font);
        input.setFont(font);
        add(label);
        add(input);
    }

    public InputField(String name, String inputText){
        setLayout(new FlowLayout(FlowLayout.TRAILING));
        label = new JLabel(name);
        input = new JTextField(inputText);
        input.setPreferredSize(new Dimension(100, 24));
        add(label);
        add(input);
    }

    public void addActionListener(ActionListener e){
        input.addActionListener(e);
    }

    public void setInputBackground(Color bg) {
        input.setBackground(bg);
    }
    public void setInputForeground(Color bg) {
        input.setForeground(bg);
    }
    public void setInputText(String txt){
        input.setText(txt);
    }
    public String getInputText(){
        return input.getText();
    }
}
