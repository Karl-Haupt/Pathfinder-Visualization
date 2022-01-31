package com.karlh;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class Controller extends JPanel implements ActionListener, ItemListener {
    private JPanel pnlInterface, pnlTop, pnlCenter, pnlBottom;
    private JRadioButton radStart, radEnd, radWall;
    private ButtonGroup buttonGroup;
    private JButton btnReset, btnStart;

    private JComboBox cbkAlgo;
    private JLabel lblHeading, lblSKey, lblEKey, lblCKey, lblBackspaceKey, lblDKey, lbl1Key, lbl2Key, lblSeperator;

    private Assessor assessor;
    private Pathfinder path;
    private boolean algoType = false;

    //algorithm time

    //Pass pathfinder into the constructor + process the pathfinder
    public Controller(Pathfinder path, Assessor assessor) {
        this.path = path;
        this.assessor = assessor;

        String[] typeOfAlgo = {"A*", "Dijkstra"};
        pnlInterface = new JPanel();
        pnlTop = new JPanel();
        pnlCenter = new JPanel();
        pnlBottom = new JPanel();

        radStart = new JRadioButton("Start Node");
        radEnd = new JRadioButton("End Node");
        radWall = new JRadioButton("Wall Node");
        buttonGroup = new ButtonGroup();

        btnReset = new JButton("Reset Path");
        btnStart = new JButton("Start Algorithm");

        cbkAlgo = new JComboBox(typeOfAlgo);

        lblHeading = new JLabel("Pathfinder Visualizer: Controls & Information");
        lblSKey = new JLabel("Hold 's' and click anywhere on the grid to create a start node.\n");
        lblEKey = new JLabel("Hold 'e' and click anywhere on the grid to create an end node.\n");
        lblCKey = new JLabel("Press 'c' to clear the grid or start over.\n");
        lblBackspaceKey = new JLabel("Press 'backspace' to completely clear the grid while keeping start/end node\n");
        lblDKey = new JLabel("Hold 'd' and click on the specific node the user wants to delete.\n");
        lbl1Key = new JLabel("Press '1' to use the Dijkstra pathfinding\n");
        lbl2Key = new JLabel("Press '2' to use the A-Star pathfinding");

        lblSeperator = new JLabel();
        lblSeperator.add(new JSeparator());

        btnStart.addActionListener(this);
        btnReset.addActionListener(this);

        setGUI();
    }

    public void setGUI() {
        this.setVisible(true);
        this.setBackground(Color.GRAY);

        buttonGroup.add(radStart);
        buttonGroup.add(radEnd);
        buttonGroup.add(radWall);

        pnlInterface.setLayout(new GridLayout(3, 1));
        pnlInterface.setBackground(Color.GRAY);

        lblHeading.setFont(new Font(Font.SERIF, Font.BOLD, 18));
        lblHeading.setForeground(Color.decode("#f5f5f5"));

        pnlTop.setLayout(new BoxLayout(pnlTop, BoxLayout.Y_AXIS));
        pnlTop.setBackground(Color.GRAY);
        pnlTop.add(lblHeading);
        pnlTop.add(Box.createVerticalStrut(10));
        pnlTop.add(lblSeperator);

        radStart.setBackground(Color.GRAY);
        radEnd.setBackground(Color.GRAY);
        radWall.setBackground(Color.GRAY);
        cbkAlgo.setBackground(Color.GRAY);

        radStart.setForeground(Color.decode("#f5f5f5"));
        radEnd.setForeground(Color.decode("#f5f5f5"));
        radWall.setForeground(Color.decode("#f5f5f5"));
        cbkAlgo.setForeground(Color.decode("#f5f5f5"));

        pnlTop.add(radStart);
        pnlTop.add(radEnd);
        pnlTop.add(radWall);
        pnlTop.add(cbkAlgo);

        pnlTop.add(Box.createVerticalStrut(20));
        pnlTop.add(new JSeparator());
        pnlTop.add(Box.createVerticalStrut(20));

        btnStart.setBackground(Color.DARK_GRAY);
        btnReset.setBackground(Color.DARK_GRAY);
        btnStart.setForeground(Color.WHITE);
        btnReset.setForeground(Color.WHITE);

        pnlCenter.setBackground(Color.GRAY);
        pnlCenter.add(btnStart);
        pnlCenter.add(btnReset);

        pnlBottom.add(Box.createVerticalStrut(20));
        pnlBottom.add(new JSeparator());
        pnlBottom.add(Box.createVerticalStrut(20));

        pnlBottom.setLayout(new BoxLayout(pnlBottom, BoxLayout.Y_AXIS));
        pnlBottom.setBackground(Color.GRAY);
        pnlBottom.add(lblSKey);
        pnlBottom.add(lblEKey);
        pnlBottom.add(lblCKey);
        pnlBottom.add(lblBackspaceKey);
        pnlBottom.add(lblDKey);
        pnlBottom.add(lbl1Key);
        pnlBottom.add(lbl2Key);

        pnlInterface.add(pnlTop, BorderLayout.NORTH);
        pnlInterface.add(pnlCenter, BorderLayout.CENTER);
        pnlInterface.add(pnlBottom, BorderLayout.SOUTH);

        this.add(pnlInterface);

    }

    public boolean isAlgoType() {
        return algoType;
    }

    public JRadioButton getRadStart() {
        return radStart;
    }

    public JRadioButton getRadEnd() {
        return radEnd;
    }

    public JRadioButton getCkbWall() {
        return radWall;
    }

    public JComboBox getCbkAlgo() {
        return cbkAlgo;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnStart) {
            if (cbkAlgo.getSelectedItem().equals("Dijkstra") && !path.isRun()) {
                path.setIsDijkstra(true);
                System.out.println("Begin Dijkstra");
            } else if(!path.isRun()) {
                path.setIsDijkstra(false);
                System.out.println("Begin A*");
            }
            assessor.runPathfinder();

        } else if(e.getSource() == btnReset) {
            assessor.clearGrid();
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if(e.getSource() == "A*") algoType = false;
        else if(e.getSource() == "Dijsktra") algoType = true;
    }
}
