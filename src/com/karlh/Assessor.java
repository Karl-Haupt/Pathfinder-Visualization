package com.karlh;

/*
* Assessor.java -> The GUI that has the grid, displays start, end and wall nodes.
* Resources ->
*       Devon Crawford(Idea) - https://www.youtube.com/watch?v=1-YPj5Vt0oQ
* */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Set;

public class Assessor extends JPanel implements ActionListener, MouseListener, KeyListener, MouseMotionListener {
    private Pathfinder path;
    private JPanel pane;
    private JFrame frame;

    private Timer timer;

    private Node start;
    private Node end;

    private char keyPress;
    private boolean isOctile;
    private boolean isManhattan;

    private static final int WIDTH = 900;
    private static final int HEIGHT = 750;
    private static final int NODE_SIZE = 25;

    private Controller controller;
    private JPanel pnlInterface;

    public Assessor() {
        frame = new JFrame();

        isOctile = true;
        isManhattan = false;

        setLayout(new BorderLayout());
        pane = new JPanel();
        setFocusable(true);

        timer = new Timer(100, this);

        path = new Pathfinder(this);

        pnlInterface = new JPanel();
        pnlInterface.setBackground(Color.GRAY);
        controller = new Controller(path, this);
        pnlInterface.add(controller);

        frame.setLayout(new GridLayout(1, 2));
        frame.add(this);
        frame.add(pnlInterface);

        frame.setTitle("PathFinder Visualization");
        frame.getContentPane().setPreferredSize(new Dimension(WIDTH, HEIGHT));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        this.addMouseListener(this);
        this.addKeyListener(this);
        this.addMouseMotionListener(this);

        //Update changes to the Grid
        this.revalidate();
        this.repaint();

        timer.start();;
    }

    @Override
    public void paintComponents(Graphics g) {
        super.paintComponents(g);
    }

    /*
    * The GUI is created in this method. Everytime a change/update happens on the gui
    * the GUI will update those changes
    * */
    public void paint(Graphics g) {
        //Draw grid for the pathfinder
        g.setColor(Color.LIGHT_GRAY);
        for(int j = 0; j < this.getHeight(); j += NODE_SIZE) {
            for(int i = 0; i < this.getWidth(); i += NODE_SIZE) {

                g.setColor(new Color(40, 42, 54));
                g.fillRect(i, j, NODE_SIZE, NODE_SIZE);
                g.setColor(Color.black);
                g.drawRect(i, j, NODE_SIZE, NODE_SIZE);
            }
        }

        drawWallNodes(g);
        drawClosedList(g);
        drawOpenList(g);
        drawFinalPath(g);

        fillStartNode(g);
        fillEndNode(g);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keyPress = e.getKeyChar();

        switch (keyPress) {
            case KeyEvent.VK_SPACE:
                //Start algorithm
                runPathfinder();

                break;
            //Backspace -> deletes the grid(Nodes)
            case KeyEvent.VK_BACK_SPACE:
                clearGrid();

                break;
            case '1':
                if (!path.isRun()) {
                    path.setIsDijkstra(true);
                    System.out.println("Dijkstra Selected. \n");
                }
                break;
            case '2':
                if (!path.isRun()) {
                    path.setIsDijkstra(false);
                    System.out.println("A-Star Selected. \n");
                }
                break;
            case 'c':
                //Clear and reset
                path.reset();
                repaint();
                break;
            case 'm':
                if(!path.isRun()) {
                    isManhattan = true;
                    isOctile = false;

                    System.out.println("Use Manhattan. \n");
                }
                break;
            case 'o':
                if(!path.isRun()) {
                    isManhattan = false;
                    isOctile = true;

                    System.out.println("Use Octile. \n");
                }
                break;
            default:
        }
    }

    /*
    * After mouse events, this method will be called and make the approximated
    * calculations and nodes. For example, clicking and 'S' will create a start node,
    * etc
    * */
    public void gridWork(MouseEvent e) {
        //Left click on mouse
        if (e.getButton() == MouseEvent.BUTTON1) {

            //mouse clicks not exactly at node point of creation, find the remainder to see what node
            //was clicked
            int xOver = e.getX() % NODE_SIZE;
            int yOver = e.getY() % NODE_SIZE;

            //left mouse and 'S' key makes start node or if you select the radio input
            if (keyPress == 's' || controller.getRadStart().isSelected()) {

                int xTmp = e.getX() - xOver;
                int yTmp = e.getY() - yOver;

                //if start is null, create the start node where end is not on the grid
                if (start == null) {
                    if (!path.isWall(new Point(xTmp, yTmp))) {
                        if (end == null) {
                            start = new Node(xTmp, yTmp);
                        } else {
                            if (!end.equals(new Node(xTmp, yTmp))) {
                                start = new Node(xTmp, yTmp);
                            }
                        }
                    }
                    //Otherwise, prevent start node from moving
                } else {
                    if (!path.isWall(new Point(xTmp, yTmp))) {
                        if (end == null) {
                            start.setXY(xTmp, yTmp);
                        } else {
                            if (!end.equals(new Node(xTmp, yTmp))) {
                                start.setXY(xTmp, yTmp);
                            }
                        }
                    }

                }

                repaint();
                //left mouse and 'E' key creates end node
            } else if (keyPress == 'e' || controller.getRadEnd().isSelected()) {

                int xTmp = e.getX() - xOver;
                int yTmp = e.getY() - yOver;

                //End is null -> create end on nodes where start not already
                if(end == null) {
                    if(!path.isWall(new Point(xTmp, yTmp))) {
                        if(start == null) end = new Node(xTmp, yTmp);
                        else {
                            if(!start.equals(new Node(xTmp, yTmp))) {
                                end = new Node(xTmp, yTmp);
                            }
                        }
                    }
                //Otherwise, prevent end node from moving
                } else {
                    if(!path.isWall(new Point(xTmp, yTmp))) {
                        if(start == null) {
                            end.setXY(xTmp, yTmp);
                        } else {
                            if(!start.equals(new Node(xTmp, yTmp))) {
                                end.setXY(xTmp, yTmp);
                            }
                        }
                    }
                }

            repaint();
            } else if(keyPress == 'd') {
                //Delete walls with this function if right click is not clicked
                int nodeX = e.getX() - xOver;
                int nodeY = e.getY() - yOver;

                //Remove the start, end or a wall
                if(start != null && start.equals(new Node(nodeX, nodeY))) start = null;
                else if(end != null && end.equals(new Node(nodeX, nodeY))) end = null;
                else path.removeWall(new Point(nodeX, nodeY));

                repaint();

            //Mouse clicks makes walls
            } else {
                //Create walls and add to the list of walls
                Node tmpWall = new Node(e.getX() - xOver, e.getY() - yOver);

                if(start == null && end == null) {
                    path.addWall(new Point(tmpWall.getX(), tmpWall.getY()));
                }

                if(!(tmpWall.equals(start)) && !(tmpWall.equals(end))) {
                    path.addWall(new Point(tmpWall.getX(), tmpWall.getY()));
                }

                repaint();
            }

        } else if(e.getButton() == MouseEvent.BUTTON1) {
            //Delete nodes with right click
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //Mouse clicks updates grid to show changes
        if(!path.isRun()) gridWork(e);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        timer.setDelay(50);

        if(path.isRun() && !path.isComplete() && !path.isPause())
            path.aStarPath();

        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        keyPress = 0;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(!path.isRun()) gridWork(e);
    }

    public boolean isOctile() {
        return isOctile;
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    public void runPathfinder() {
        //Set the start & end node for the path

        //On the initial run, set start and end; Do again when the algorithm is finished
        if(!path.isComplete() && !path.isRun() && start != null && end != null ) {
            path.setIsRun(true);
            path.setStart(start);
            path.setEnd(end);
        }

        //Space -> Pause the algorithm
        if(!path.isPause()) path.setIsPause(true);
        else if(path.isPause()) path.setIsPause(false);
    }

    public void clearGrid() {
        if(!path.isRun()) {
            path.deleteWalls(true);
            path.reset();

            System.out.println("Grid deletion complete. \n");
        }
    }

    //Fills all the wall nodes to dark grey
    private void drawWallNodes(Graphics g) {
        //Draw the wall nodes
        Set<Point> wallList = path.getWall();
        g.setColor(new Color(68, 71, 90));
        for(Point pt : wallList) {
            int xCoord = (int) pt.getX();
            int yCoord = (int) pt.getY();

            g.fillRect(xCoord + 1, yCoord + 1, NODE_SIZE - 2, NODE_SIZE - 2);
        }
    }

    //Fills all the nodes that have been searched to red
    private void drawClosedList(Graphics g) {
        Set<Point> closedList = path.getClosed();
        g.setColor(new Color(253, 90, 90));
        for(Point pt : closedList) {
            int xCoord = (int) pt.getX();
            int yCoord = (int) pt.getY();

            g.fillRect(xCoord + 1, yCoord + 1, NODE_SIZE - 2, NODE_SIZE - 2);
        }
    }

    //Fills all the neighbouring nodes that still need to be searched to green
    private void drawOpenList(Graphics g) {
        PriorityQueue<Node> openList = path.getOpen();
        g.setColor(new Color(0,191,255));
        for(Node e : openList) {
            g.fillRect(e.getX() + 1, e.getY() + 1, NODE_SIZE - 2, NODE_SIZE - 2);
        }
    }

    //Fills all the nodes that are in the final path to purple
    private void drawFinalPath(Graphics g) {
        ArrayList<Node> finalPath = path.getFinalPath();
        g.setColor(new Color(255,255,0));
        for(int i = 0; i < finalPath.size(); i++) {
            g.fillRect(finalPath.get(i).getX() + 1, finalPath.get(i).getY() + 1,
                    NODE_SIZE - 2, NODE_SIZE - 2);
        }
    }

    //Fills the start node to blue
    private void fillStartNode(Graphics g) {
        if(start != null) {
            g.setColor(new Color(0,255,0));
            g.fillRect(start.getX() + 1, start.getY() + 1, NODE_SIZE - 2, NODE_SIZE -
                    2);
        }

    }

    //Fills the end node to pink
    private void fillEndNode(Graphics g) {
        if(end != null) {
            g.setColor(new Color(255, 121, 198));
            g.fillRect(end.getX() + 1, end.getY() + 1, NODE_SIZE - 2, NODE_SIZE - 2);
        }
    }

    public static void main(String[] args) {
        new Assessor();
    }
}