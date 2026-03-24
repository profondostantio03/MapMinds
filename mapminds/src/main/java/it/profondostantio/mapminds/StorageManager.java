package it.profondostantio.mapminds;

import java.io.*;

public class StorageManager {

    public static void save(Node root, String fileName) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(root);
        }
    }

    public static Node load(String fileName) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            return (Node) in.readObject(); // Necessita di Cast (Lez10)
        }
    }
}