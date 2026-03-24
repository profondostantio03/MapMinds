import java.io.*;

public class StorageManager {
    public static void saveMap(MindMap map, String fileName) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(map);
        }
    }

    public static MindMap loadMap(String fileName) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            return (MindMap) in.readObject(); 
        }
    }
}