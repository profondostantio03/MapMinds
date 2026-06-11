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

    private String color = "#add8e6"; // Colore di default del nodo (LightBlue)
    private String bgColor = "#fdfdfd";

    private String note = "";

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

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getBgColor() { return bgColor; }
    public void setBgColor(String bgColor) { this.bgColor = bgColor; }
}