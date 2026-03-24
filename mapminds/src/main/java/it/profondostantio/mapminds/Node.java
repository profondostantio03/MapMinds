package it.profondostantio.mapminds;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Node implements Serializable {
    private String text;
    private double x, y; 
    private List<Node> children; 

    public Node(String text, double x, double y) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.children = new ArrayList<>(); 
    }

    // incapsulamento: metodi pubblici per accedere a variabili private
    public void addChild(Node child) { children.add(child); }
    public String getText() { return text; }
    public double getX() { return x; }
    public double getY() { return y; }
    public List<Node> getChildren() { return children; }
    
    // setter per lo spostamento (fondamentale per il drag and drop)
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
}