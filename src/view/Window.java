package view;

import controller.Controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Observable;
import java.util.Observer;
import java.util.Stack;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import javax.swing.JRadioButton;

import model.AndrewsAlgorithm;
import model.Point;

public class Window extends JFrame implements Observer{
    private Controller controller;
    
    private JPanel buttonPanel = new JPanel();
    private JButton randomButton = new JButton("Random points");
    private JButton readFromFileButton = new JButton("Points from file");
    private JButton execButton = new JButton("Execute");
    private JButton stopExecButton = new JButton("Next step");
    private DrawingPanel drawingPanel = new DrawingPanel();
    private JRadioButton melkmansButton = new JRadioButton("Melkmans algo");
    private JRadioButton andrewsButton = new JRadioButton("Andrews algo");
    private ButtonGroup algorithm = new ButtonGroup();
    
    public Window(Controller aController) {
        //Basic window config
        super("Lab1: Computing convex hull");
        this.controller = aController;
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(1200, 700));
        this.pack();
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

        //set Buttons
        randomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                controller.createRandomSet();
            }
        });
        readFromFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(Window.this);
                if(returnValue == JFileChooser.APPROVE_OPTION) {
                    controller.loadSetFromFile(fileChooser.getSelectedFile().getPath());
                }
            }
        });
        execButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(controller.isReadyToExecute() && !controller.isComputing()) {
                    stopExecButton.setText("Stop execution");
                    controller.execute();
                }
            }
        });
        stopExecButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(controller.isComputing()) {
                    stopExecButton.setText("Next step");
                    controller.stopExec();
                } else {
                    controller.nextStep();
                }
            }
        });
        melkmansButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(!controller.isComputing())
                    controller.setAlgorithmToMelkmans(true);
                else
                    andrewsButton.setSelected(true);
            }
        });
        andrewsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(!controller.isComputing())
                    controller.setAlgorithmToMelkmans(false);
                else
                    melkmansButton.setSelected(true);
            }
        });
        algorithm.add(melkmansButton);
        algorithm.add(andrewsButton);
        andrewsButton.setSelected(true);
        
        buttonPanel.setLayout(new GridLayout(5,1));
        buttonPanel.add(randomButton);
        buttonPanel.add(readFromFileButton);
        buttonPanel.add(execButton);
        buttonPanel.add(stopExecButton);
        JPanel algoPanel = new JPanel();
        algoPanel.setLayout(new GridLayout(2,1));
        algoPanel.add(melkmansButton);
        algoPanel.add(andrewsButton);
        buttonPanel.add(algoPanel);

        this.add(buttonPanel, BorderLayout.WEST);
        
        //set drawing panel
        drawingPanel.setBackground(Color.WHITE);
        this.add(drawingPanel, BorderLayout.CENTER);
    }

    @Override
    public void update(Observable observable, Object object) {
        // The controller started another thread to compute a new hull
        ((Observable)object).addObserver(drawingPanel);
    }
}
