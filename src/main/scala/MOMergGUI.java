import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class MOMergGUI extends JFrame{
    private JPanel mainPanel;
    private JLabel O1Label;
    private JButton uploadButton;
    private JLabel O2Label;
    private JTextField textField1;
    private JButton openButton1;
    JComboBox comboBoxLanguage1;
    private JButton openButton2;
    private JTextField textField2;
    private JComboBox comboBoxLanguage2;
    JComboBox comboBox1;
    public String selectedFilePath;


    //    public MOMergGUI(){
//
//    }
    public MOMergGUI(){

//        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();
//        String country[]={"India","Aus","U.S.A","England","Newzealand"};
//        comboBoxLanguage1 = new JComboBox(country);
//        comboBoxLanguage1.setBounds(50, 50,90,20);
        comboBoxLanguage1.addItem("Choose a language");
        comboBoxLanguage1.addItem("English");
        comboBoxLanguage1.addItem("Arabic");
        comboBoxLanguage1.addItem("German");
        comboBoxLanguage1.addItem("French");

        String[] bookTitles = new String[] {"Effective Java", "Head First Java",
                "Thinking in Java", "Java for Dummies"};

        String[] petStrings = { "Bird", "Cat", "Dog", "Rabbit", "Pig" };

//Create the combo box, select item at index 4.
//Indices start at 0, so 4 specifies the pig.
        comboBox1 = new JComboBox(petStrings);
        comboBox1.setSelectedIndex(4);
//        comboBox1.addActionListener(this);

// add to the parent container (e.g. a JFrame):
//        add(comboBoxLanguage2);

        //Create the combo box, select the item at index 4.
        //Indices start at 0, so 4 specifies the pig.
//        comboBoxLanguage1 = new JComboBox(petStrings);
//        comboBoxLanguage1.setSelectedIndex(4);


        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "ttl & NT files", "ttl", "nt");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(mainPanel);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    selectedFilePath = chooser.getSelectedFile().toString();
                    System.out.println("You chose to open this file: " + selectedFilePath);
//                    OntoMerge Om = new OntoMerge();
//                    Om.OnMerge(selectedFilePath);

                }
            }
        });
        comboBox1.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {

            }
        });
        comboBox1.addComponentListener(new ComponentAdapter() {
        });
        comboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    public static void main(String[] args){
        JFrame frame = new MOMergGUI();
        frame.setVisible(true);
//        OntoMerge Om = new OntoMerge();
        new MOMergGUI();
//        Om.OnMerge(gui.selectedFilePath);

        // add ItemListener
//        comboBoxLanguage1.addItemListener(gui);
    }


}

