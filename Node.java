import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Node implements Serializable {
    private String text;
    private double x, y; 
    private List<Node> children; // coesione: il nodo sa chi sono i suoi figli

    public Node(String text, double x, double y) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.children = new ArrayList<>(); 
    }

    // metodo per aggiungere un sotto-nodo
    public void addChild(Node child) {
        this.children.add(child);
    }

    // incapsulamento, getters e setters
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public double getX() { return x; }
    public double getY() { return y; }

    public int countSubTree() { 
        int count = 1; 
        for (Node child : children) {
            count += child.countSubTree(); // chiamata ricorsiva
        }
        return count;
    }

    // polimorfismo: sovrascriviamo toString della classe Object 
    @Override
    public String toString() {
        return "Nodo: " + text + " (Figli: " + children.size() + ")";
    }
}