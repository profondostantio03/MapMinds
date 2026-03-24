package it.profondostantio.mapminds;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.IOException;

public class MindMapApp extends Application {
    private Stage stage; 
    private Pane rootPane = new Pane();
    private Node mainRootNode;
    private Node selectedNode;
    
    private Map<Node, StackPane> visualNodes = new HashMap<>();
    private Map<Node, Line> parentLines = new HashMap<>();

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        mostraMenuPrincipale();
    }

    private void mostraMenuPrincipale() {
        // pulizia pre-menu per evitare conflitti di eventi grafici
        rootPane.getChildren().clear();
        selectedNode = null;

        VBox menu = new VBox(20);
        menu.setStyle("-fx-alignment: center; -fx-padding: 50; -fx-background-color: #f4f4f4;");
        Text titolo = new Text("MapMinds Manager");
        titolo.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");

        Button btnNuova = new Button("Crea Nuova Mappa");
        Button btnCarica = new Button("Carica Mappa");
        
        btnNuova.setOnAction(e -> setupNuovaMappa());
        btnCarica.setOnAction(e -> caricaMappaEsistente());

        menu.getChildren().addAll(titolo, btnNuova, btnCarica);
        Scene menuScene = new Scene(menu, 800, 600);
        
        stage.setTitle("MapMinds - Benvenuto");
        stage.setScene(menuScene);
        stage.show();
    }

    private void setupNuovaMappa() {
        TextInputDialog dialog = new TextInputDialog("Idea Centrale");
        dialog.setTitle("Nuova Mappa");
        dialog.setHeaderText("Inizia un nuovo progetto");
        dialog.showAndWait().ifPresent(name -> {
            mainRootNode = new Node(name, 400, 300);
            avviaEditor();
        });
    }

    private void caricaMappaEsistente() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Apri File Mappa");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Dati Mappa (*.dat)", "*.dat"));
        
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                Node caricato = StorageManager.load(file.getAbsolutePath());
                if (caricato != null) {
                    mainRootNode = caricato;
                    selectedNode = null;
                    
                    // IL FIX: Platform.runLater evita crash di concorrenza eventi mouse
                    Platform.runLater(() -> {
                        avviaEditor();
                        System.out.println("DEBUG: Mappa caricata con successo.");
                    });
                }
            } catch (Exception ex) { 
                System.err.println("Errore nel caricamento: " + ex.getMessage());
                ex.printStackTrace(); 
            }
        }
    }

    private void avviaEditor() {
        renderAll(); 
        Scene editorScene = new Scene(rootPane, 800, 600);
        
        editorScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.N && selectedNode != null) creaFiglio(selectedNode);
            else if (e.getCode() == KeyCode.S) salvaConNome();
            else if (e.getCode() == KeyCode.ESCAPE) mostraMenuPrincipale();
            else if (e.getCode() == KeyCode.DELETE || e.getCode() == KeyCode.BACK_SPACE) cancellaNodo();
        });

        stage.setTitle("MapMinds Editor - [ESC] Menu | [S] Salva | [N] Nuovo | [CANC] Elimina");
        stage.setScene(editorScene);
    }

    private void renderAll() {
        rootPane.getChildren().clear();
        visualNodes.clear();
        parentLines.clear();
        if (mainRootNode != null) {
            renderRecursive(mainRootNode);
        }
    }

    private void renderRecursive(Node node) {
        // grafica nodi
        StackPane container = new StackPane();
        Shape shape;
        switch (node.getShape()) {
            case "SQUARE": shape = new Rectangle(80, 80); break;
            case "HEXAGON": shape = createHexagon(45); break;
            default: shape = new Circle(40); break;
        }

        shape.setFill((node == selectedNode) ? Color.ORANGE : Color.LIGHTBLUE);
        shape.setStroke(Color.BLACK);
        shape.setStrokeWidth((node == selectedNode) ? 3 : 1);

        Text label = new Text(node.getText());
        label.setMouseTransparent(true);
        container.getChildren().addAll(shape, label);

        container.setLayoutX(node.getX() - 40);
        container.setLayoutY(node.getY() - 40);

        // EVENTI NODO
        container.setOnMousePressed(e -> {
            selectedNode = node;
            aggiornaColoriSelezione();
            if (e.getButton() == MouseButton.SECONDARY) mostraMenuContesto(node, e.getScreenX(), e.getScreenY());
            else if (e.getClickCount() == 2) rinominaNodo(node);
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

        // 2. ARCHI (Linee)
        for (Node child : node.getChildren()) {
            Line line = new Line(node.getX(), node.getY(), child.getX(), child.getY());
            line.setStrokeWidth(2);
            if ("DASHED".equals(child.getLineStyle())) line.getStrokeDashArray().addAll(10d, 5d);
            
            line.setOnMouseClicked(e -> {
                child.setLineStyle(child.getLineStyle().equals("SOLID") ? "DASHED" : "SOLID");
                renderAll();
            });

            rootPane.getChildren().add(0, line); // mette le linee sotto i nodi
            parentLines.put(child, line); 
            renderRecursive(child);
        }

        rootPane.getChildren().add(container);
        visualNodes.put(node, container);
    }

    private void updateLinesForNode(Node node) {
        Line toParent = parentLines.get(node);
        if (toParent != null) {
            Node padre = trovaPadre(mainRootNode, node);
            if (padre != null) {
                toParent.setStartX(padre.getX());
                toParent.setStartY(padre.getY());
            }
            toParent.setEndX(node.getX());
            toParent.setEndY(node.getY());
        }
        for (Node child : node.getChildren()) {
            Line toChild = parentLines.get(child);
            if (toChild != null) {
                toChild.setStartX(node.getX());
                toChild.setStartY(node.getY());
                toChild.setEndX(child.getX());
                toChild.setEndY(child.getY());
            }
        }
    }

    private void aggiornaColoriSelezione() {
        for (Node n : visualNodes.keySet()) {
            StackPane sp = visualNodes.get(n);
            Shape s = (Shape) sp.getChildren().get(0);
            s.setFill(n == selectedNode ? Color.ORANGE : Color.LIGHTBLUE);
            s.setStrokeWidth(n == selectedNode ? 3 : 1);
        }
    }

    private Polygon createHexagon(double r) {
        Polygon hex = new Polygon();
        for (int i = 0; i < 6; i++) {
            hex.getPoints().addAll(r * Math.cos(i * Math.PI / 3), r * Math.sin(i * Math.PI / 3));
        }
        return hex;
    }

    private void mostraMenuContesto(Node n, double x, double y) {
        ContextMenu ctx = new ContextMenu();
        MenuItem c = new MenuItem("Cerchio");
        MenuItem s = new MenuItem("Quadrato");
        MenuItem h = new MenuItem("Esagono");
        c.setOnAction(e -> { n.setShape("CIRCLE"); renderAll(); });
        s.setOnAction(e -> { n.setShape("SQUARE"); renderAll(); });
        h.setOnAction(e -> { n.setShape("HEXAGON"); renderAll(); });
        ctx.getItems().addAll(c, s, h);
        ctx.show(stage, x, y);
    }

    private void rinominaNodo(Node n) {
        TextInputDialog d = new TextInputDialog(n.getText());
        d.showAndWait().ifPresent(txt -> { n.setText(txt); renderAll(); });
    }

    private void creaFiglio(Node parent) {
        TextInputDialog d = new TextInputDialog("Nuovo");
        d.showAndWait().ifPresent(n -> {
            int i = parent.getChildren().size();
            double a = i * (Math.PI / 4);
            parent.addChild(new Node(n, parent.getX() + Math.cos(a)*180, parent.getY() + Math.sin(a)*180));
            renderAll();
        });
    }

    private void cancellaNodo() {
        if (selectedNode != null && selectedNode != mainRootNode) {
            Node padre = trovaPadre(mainRootNode, selectedNode);
            if (padre != null) {
                padre.getChildren().remove(selectedNode);
                selectedNode = null;
                renderAll();
            }
        }
    }

    private void salvaConNome() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Salva Mappa");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Dati Mappa (*.dat)", "*.dat"));
        File f = fc.showSaveDialog(stage);
        if (f != null) {
            try { 
                StorageManager.save(mainRootNode, f.getAbsolutePath()); 
                System.out.println("Mappa salvata: " + f.getName());
            } catch (IOException ex) { ex.printStackTrace(); }
        }
    }

    private Node trovaPadre(Node current, Node target) {
        for (Node child : current.getChildren()) {
            if (child == target) return current;
            Node trovato = trovaPadre(child, target);
            if (trovato != null) return trovato;
        }
        return null;
    }

    public static void main(String[] args) { launch(args); }
}