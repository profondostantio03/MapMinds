package it.profondostantio.mapminds;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Node implements Serializable {
    private static final long serialVersionUID = 1L;

    private String text;
    private double x;
    private double y;
    private List<Node> children;

    private String shape = "CIRCLE";
    private String lineStyle = "SOLID";

    public Node(String text, double x, double y) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.children = new ArrayList<>();
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public double getX() { return x; }
    public double getY() { return y; }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public List<Node> getChildren() { return children; }

    public void addChild(Node child) {
        this.children.add(child);
    }

    // metodi per la forma del nodo
    public String getShape() { return shape; }
    public void setShape(String shape) { this.shape = shape; }

    // metodi per lo stile della linea
    public String getLineStyle() { return lineStyle; }
    public void setLineStyle(String lineStyle) { this.lineStyle = lineStyle; }
}