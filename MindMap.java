import java.io.Serializable;

public class MindMap implements Serializable {
    private Node root;

    public MindMap(String rootTitle) {
        this.root = new Node(rootTitle, 0, 0);
    }

    public Node getRoot() {
        return root;
    }
}