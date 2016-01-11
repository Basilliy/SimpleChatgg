package forSnake;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ServerThread extends Thread {

    private Socket append;
    private boolean flag = false;

    private ObjectOutputStream outS;
    private ObjectInputStream inS;

    public ServerThread() {
        super("ServerThread");

        try {
            PipedOutputStream pOut = new PipedOutputStream();
            PipedInputStream pIn = new PipedInputStream(pOut);
            ObjectOutputStream outS = new ObjectOutputStream(pOut);
            ObjectInputStream inS = new ObjectInputStream(pIn);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void run() {

    }

    public void add(Socket socket) {
        if (!flag) {
            append = socket;
            flag = true;
        }
    }

}
