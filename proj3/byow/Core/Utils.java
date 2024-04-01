package byow.Core;

import java.io.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Utils {
    public static void save(Object obj) throws IOException, FileNotFoundException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("SAVEDWORLD.txt"));
        out.writeObject(obj);
        out.close();
    }
    public static WorldInfo read() throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream("SAVEDWORLD.txt"));
        WorldInfo returned = (WorldInfo) in.readObject();
        return returned;
    }

}
