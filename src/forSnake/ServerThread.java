package forSnake;

import java.io.*;
import java.util.ArrayList;

public class ServerThread extends Thread {

    public boolean listening = true;
    private final ArrayList<ObjectOutputStream> oos;
    private final ObjectInputStream in;
    private final ObjectOutputStream forRemove;
    private String name;

    public ServerThread(ArrayList<ObjectOutputStream> oos, ObjectInputStream in,
                        ObjectOutputStream forRemove) {
        this.oos = oos;
        this.in = in;
        this.forRemove = forRemove;
    }

    @Override
    public synchronized void run() {
        boolean bName = true;
        while (listening) {
            try {
                Object object = in.readObject();
                if (bName) {
                    if (object.getClass().equals(String.class)) {
                        String s = (String) object;
                        name = s.substring("В комнату вошел игрок ".length(), s.length() - 1);
                    }
                    bName = false;
                }
                synchronized (oos) {
                    for (ObjectOutputStream out : oos) {
                        out.writeObject(object);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                synchronized (oos) {
                    oos.remove(forRemove);
                    System.out.println("ServerThread " + oos.size());
                    for (ObjectOutputStream out : oos) {
                        try {
                            out.writeObject("Игрок " + name + " покинул комнату\n");
                        } catch (IOException e1) { /*Nothing TO DO */ }
                    }
                }
                break;
            }
        }
    }
}
