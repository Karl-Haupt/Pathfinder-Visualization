package com.karlh;

public class Node {

    //Location of nodes
    private int x;
    private int y;

    //f = total cost of the node
    //g = the cost from node a, b and c
    //h = total cost of nodes
    private double f, g, h;
    private Node parent;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    //Setters
    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setF(double f) {
        this.f = f;
    }

    public void setG(double g) {
        this.g = g;
    }

    public void setH(double h) {
        this.h = h;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    //Getters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getF() {
        return f;
    }

    public double getG() {
        return g;
    }

    public double getH() {
        return h;
    }

    public Node getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return "Node{" +
                "x=" + x +
                ", y=" + y +
                ", F cost: " + f +
                ", G cost: " + g +
                ", H cost:=" + h +
                ", parent=" + parent +
                "} \n";
    }

    public boolean equals(Object other) {
        if(other == null) {
            return false;
        }

        Node tmp = (Node) other;
        if(this.x == tmp.getX() && this.y == tmp.getY()) {
            return true;
        }

        return false;
    }
}
