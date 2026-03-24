package it.profondostantio.mapminds;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;

public class MindMapApp extends Application {
    private Pane rootPane = new Pane();
    private Node mainRootNode;
    private Node selectedNode;
    
    // mappe per l'accesso rapido agli oggetti grafici 
    private Map<Node, StackPane> visualNodes = new HashMap<>();
    private Map<Node, Line> parentLines = new HashMap<>();

    @Override
    public void start(Stage primaryStage) {
        TextInputDialog rootDialog = new TextInputDialog("Idea Centrale");
        rootDialog.setTitle("Nuova Mappa");
        rootDialog.setHeaderText("Benvenuto");
        rootDialog.setContentText("Argomento principale:");

        rootDialog.showAndWait().ifPresent(name -> {
            mainRootNode = new Node(name, 400, 300);
            renderAll(); 

            Scene scene = new Scene(rootPane, 800, 600);
            
            // Gestione Tastiera
            scene.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.N && selectedNode != null) {
                    creaFiglio(selectedNode);
                }
            });

            primaryStage.setTitle("MapMinds - Seleziona un nodo e premi 'N'");
            primaryStage.setScene(scene);
            primaryStage.show();
        });
    }

    private void renderAll() {
        rootPane.getChildren().clear();
        visualNodes.clear();
        parentLines.clear();
        renderRecursive(mainRootNode);
    }

    private void renderRecursive(Node node) {
        // creazione linee sotto i nodi
        for (Node child : node.getChildren()) {
            Line line = new Line(node.getX(), node.getY(), child.getX(), child.getY());
            line.setStrokeWidth(2);
            rootPane.getChildren().add(line);
            parentLines.put(child, line); 
            renderRecursive(child);
        }

        // creazione nodo
        StackPane container = new StackPane();
        Circle circle = new Circle(40, (node == selectedNode) ? Color.ORANGE : Color.LIGHTBLUE);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth((node == selectedNode) ? 3 : 1);
        
        Text label = new Text(node.getText());
        label.setMouseTransparent(true);
        container.getChildren().addAll(circle, label);

        // posizionamento default
        container.setLayoutX(node.getX() - 40);
        container.setLayoutY(node.getY() - 40);

        container.setOnMousePressed(e -> {
            if (selectedNode != node) {
                selectedNode = node;
                renderAll(); 
            }
            e.consume();
        });

        container.setOnMouseDragged(e -> {
            double newX = e.getSceneX() - rootPane.localToScene(0, 0).getX();
            double newY = e.getSceneY() - rootPane.localToScene(0, 0).getY();

            node.setPosition(newX, newY);

            container.setLayoutX(newX - 40);
            container.setLayoutY(newY - 40);

            updateLinesForNode(node);
        });

        rootPane.getChildren().add(container);
        visualNodes.put(node, container);
    }

    // aggiorna solo le linee che toccano il nodo mosso 
    private void updateLinesForNode(Node node) {
        Line toParent = parentLines.get(node);
        if (toParent != null) {
            toParent.setEndX(node.getX());
            toParent.setEndY(node.getY());
        }

        // linee che vanno ai figli
        for (Node child : node.getChildren()) {
            Line toChild = parentLines.get(child);
            if (toChild != null) {
                toChild.setStartX(node.getX());
                toChild.setStartY(node.getY());
            }
        }
    }

    private void creaFiglio(Node parent) {
        TextInputDialog d = new TextInputDialog("Sotto-argomento");
        d.setTitle("Nuovo Nodo");
        d.setHeaderText("Aggiungi a: " + parent.getText());
        d.showAndWait().ifPresent(n -> {
            if (!n.trim().isEmpty()) {
                parent.addChild(new Node(n, parent.getX() + 100, parent.getY() + 100));
                renderAll(); // Qui serve ricaricare tutto perché la struttura è cambiata
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}