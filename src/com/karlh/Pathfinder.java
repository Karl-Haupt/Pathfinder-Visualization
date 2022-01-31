package com.karlh;

/*
* Pathfinder.java -> is the implementation of the A* algorithm &
* Dijkstra's algorithm
* 2021/09/16
* */

import javax.swing.*;
import java.awt.Point;
import java.util.*;

public class Pathfinder {
    //Setup for A* algo
    private static final int NODE_SIZE = 25;
    private static final int DIAGONAL_MOVE = (int) (Math.sqrt(1250));

    private Assessor assessor;
    private Node start;

    private Node end;
    private boolean deleteWalls, complete, isPause, run, isDijkstra;

    //openSet
    private PriorityQueue<Node> open;
    private Set<Point> closed, wall;
    private ArrayList<Node> finalPath;

    //Subclass needed for the Dijkstra's algo
    public class DFSFinder {
        private ArrayList<Node> finalPath;
        private Stack<Node> open;
        private Node start, end;
        private Set<Point> wall, closed;

        private Assessor assessor;

        public DFSFinder(Assessor assessor) {
            this.assessor = assessor;

            finalPath = new ArrayList<Node>();
            wall = new HashSet<Point>();
            open = new Stack<Node>();
            closed = new HashSet<Point>();
        }
    }

    //Subclass used to compare Nodes
    public class NodeComparator implements Comparator<Node> {
        public int compare(Node xCoordinates, Node yCoordinates) {
            if(xCoordinates.getF() > yCoordinates.getF()) {
                return 1;
            } else if (xCoordinates.getF() < yCoordinates.getF()) {
                return -1;
            } else {

                if(xCoordinates.getG() > yCoordinates.getG()) {
                    return 1;
                } else if(xCoordinates.getG() < yCoordinates.getG()) {
                    return -1;
                }
            }

            return 0;
        }
    }

    //Constructor
    public Pathfinder(Assessor v) {
        this.assessor = v;

        run = false;
        isPause = true;

        finalPath = new ArrayList<Node>();
        wall = new HashSet<Point>();
        open = new PriorityQueue<Node>(new NodeComparator());
        closed = new HashSet<Point>();
    }

    //Examines the wall hashset to see if the list of walls contains a specific node
    public boolean isWall(Point point) {
        return  wall.contains(point);
    }

    //Examines the closed hashset to see if nodes are in the closedSet
    public boolean closedContains(Point point) {
        return closed.contains(point);
    }

    public boolean closedRemove(Point point) {
        return closed.remove(point);
    }

    //Checks to see if the nodes are in the openSet
    public boolean openContains(Node node) {
        return open.contains(node);
    }

    public boolean openRemove(Node node) {
        return open.remove(node);
    }

    //Finds the Node in the openSet
    public Node openFind(Node node) {
        for(Node x : open) {
            if(x.equals(node)) return x;
        }

        return null;
    }

    public boolean addWall(Point point) {
        return wall.add(point);
    }

    public boolean removeWall(Point point) {
        return wall.remove(point);
    }

    public void deleteWalls(boolean checkNode) {
        deleteWalls = checkNode;
    }

    public void setIsDijkstra(boolean check) {
        isDijkstra = check;
    }

    //Reset the algorithm
    public void reset() {
        run = false;
        isPause = true;
        complete = false;

        if(deleteWalls) {
            wall.clear();
            deleteWalls = false;
        }

        closed.clear();
        open.clear();
        finalPath.clear();
    }

    //Getters and Setters to get/set the various lists containing the nodes
    public Set<Point> getWall() {
        return wall;
    }

    public PriorityQueue<Node> getOpen() {
        return open;
    }

    public Set<Point> getClosed() {
        return closed;
    }

    public ArrayList<Node> getFinalPath() {
        return finalPath;
    }

    public void setStart(Node start) {
        this.start = new Node(start.getX(), start.getY());
        open.add(this.start);
    }

    public void setEnd(Node end) {
        this.end = new Node(end.getX(), end.getY());
    }

    public void setIsPause(boolean isPause) {
        this.isPause = isPause;
    }

    public void setIsRun(boolean run) {
        this.run = run;
    }

    public boolean isRun() {
        return run;
    }

    public boolean isPause() {
        return isPause;
    }

    public boolean isComplete() {
        return complete;
    }

    //Construct the final path from the end node back to the start node - method is called once their is a valid path
    public void constructPath() {
        Node current = end;
        while(!(current.getParent().equals(start))) {
            finalPath.add(0, current.getParent());
            current = current.getParent();
        }

        finalPath.add(0, current);
    }

    /*
    * Cost associated with moving from the current node to the neighbour node(the formula
    * for the distance between 2 Points)
    */
    public double gCostMovement(Node parent, Node neighbor) {
        //distance from point to point in a grid
        int xCoord = neighbor.getX() - parent.getX();
        int yCoord = neighbor.getY() - parent.getY();

        return (int) (Math.sqrt(Math.pow(xCoord, 2) + Math.pow(yCoord, 2)));
    }

    /*
    * Heuristic cost is associated with moving from the neighbour node to the end node.
    *
    *
    * The heuristic uses octile distance where the cost of an orthogonal move
     * is one and the cost of a diagonal is sqrt(2). - Please refer to ______
    * */
    public double hCostForMovement(Node neighbor) {
        int hXCost = Math.abs(end.getX() - neighbor.getX());
        int hYCost = Math.abs(end.getY() - neighbor.getY());
        double hCost = hXCost + hYCost;

        if(assessor.isOctile()) {
            if(hXCost > hYCost) {
                hCost = ((hXCost - hYCost) + Math.sqrt(2) * hYCost);
            } else {
                hCost = ((hYCost - hXCost) + Math.sqrt(2) * hXCost);
            }
        }

        return hCost;
    }

    /*
    * A* Pathfinding Algorithm - tries to explore the fewest number of nodes to reach the end node.
    * Heuristic cost function h allows the path to self correct to the end node using h.
    * */
    public void aStarPath() {
        //Get the node of the lowest Priority Queue
        Node current = open.poll();

        //If their is no min, then no path
        if(current == null) {
            System.out.println("No path");
            run = false;
            isPause = true;
            return;
        }

        //If the min node is at the end, stop the algorithm and construct finalPath
        if(!isDijkstra && current.equals(end)) {
            end.setParent(current.getParent());
            run = false;
            isPause = false;
            complete = true;
            assessor.repaint();
            constructPath();
            displayAlgoEfficiency();
            return;
        }

        closed.add(new Point(current.getX(), current.getY()));

        //Calculate the costs of the 8 possible adjacent nodes to current
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {

                //skip the current node we are exploring
                if(i == 1 && j == 1) {
                    continue;
                }

                int xCoord = (current.getX() - NODE_SIZE) + (NODE_SIZE * i);
                int yCoord = (current.getY() - NODE_SIZE) + (NODE_SIZE * j);
                Node neighbor = new Node(xCoord, yCoord);

                //For Dijkstra, the shortest path is when we encounter the end node
                if(isDijkstra && neighbor.equals(end)) {
                    end.setParent(current);
                    run = false;
                    isPause = true;
                    complete = true;
                    assessor.repaint();
                    constructPath();
                    displayAlgoEfficiency();
                    return;
                }

                //Checks to see if the node is in the canvas boundary
                if(xCoord < 0 || yCoord < 0 || xCoord >= assessor.getWidth() || yCoord >= assessor.getHeight()) continue;

                //Checks to see if the neighbour node is a wall - in the open/closed set
                if(isWall(new Point(neighbor.getX(), neighbor.getY()))) continue;

                int wallJumpX = current.getX() + (xCoord - current.getX());
                int wallJumpY = current.getY() + (yCoord - current.getY());

                //Checks for the border in the adjacent pass and prevents diagonal jump across a border
                if(isWall(new Point(wallJumpX, current.getY())) || isWall(new Point(current.getX(), wallJumpY))
                    && ((j == 0 | j == 2) && i != 1)) continue;

                //Calculate the f, g, and h cost for the current node
                double gCost = current.getG() + gCostMovement(current, neighbor);
                double hCost = hCostForMovement(neighbor);
                double fCost = gCost + hCost;

                boolean inOpen = openContains(neighbor);
                boolean inClosed = closedContains(new Point(neighbor.getX(), neighbor.getY()));
                Node found = openFind(neighbor);

                //Search in Open set & in Closed set just in cases of lower gCost
                //If node inOpen and we found a lower cost, no need to search neighbour
                if(inOpen && (gCost < found.getG())) {
                    openRemove(found);
                    neighbor.setG(gCost);
                    neighbor.setF(gCost + found.getH());
                    neighbor.setParent(current);
                    open.add(neighbor);
                    continue;
                }

                //If neighbour in Closed set and found lower gCost, visit again
                if(inClosed && (gCost < neighbor.getG())) {
                    System.out.println("HEYClosed");
                    continue;
                }

                //If neighbour is not visited, then add to the open list
                if(!inOpen && !inClosed) {
                    if(isDijkstra) {
                        neighbor.setG(gCost);
                        neighbor.setF(gCost);
                    } else {
                        neighbor.setG(gCost);
                        neighbor.setH(hCost);
                        neighbor.setF(fCost);
                    }

                    neighbor.setParent(current);

                    open.add(neighbor);
                }
            }
        }
    }

    private void displayAlgoEfficiency() {
        System.out.println("Total Cost of Path: " + end.getParent().getG());
        System.out.println("Size of Open: " + open.size());
        System.out.println("Size of Closed: " + closed.size());
        System.out.println("Size of Path: " + finalPath.size() + "\n");
        JOptionPane.showMessageDialog(null,
                "Total cost of Path: " + end.getParent().getG() + "\n" +
                        "Size of Open(Blue): " + open.size() + "\n" +
                        "Size of Closed(Red): " + closed.size() + "\n" +
                        "Size of the path: " + finalPath.size() + "\n");
    }

}
