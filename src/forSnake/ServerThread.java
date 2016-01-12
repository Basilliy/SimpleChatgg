package forSnake;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

public class ServerThread extends Thread {

    private boolean listening = true;
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
