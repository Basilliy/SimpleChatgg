package forSnake;

import java.io.*;
import java.util.ArrayList;

public class ServerThread extends Thread {

    public boolean listening = true;
    private final ArrayList<ObjectOutputStream> oos;
    private final ObjectInputStream in;

    public ServerThread(ArrayList<ObjectOutputStream> oos, ObjectInputStream in) {
        this.oos = oos;
        this.in = in;
    }

    @Override
    public synchronized void run() {
        while (listening) {
            try {
                Object object = in.readObject();
                synchronized (oos) {
                    for (ObjectOutputStream out: oos){
                        out.writeObject(object);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
