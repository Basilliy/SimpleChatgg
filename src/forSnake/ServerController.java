package forSnake;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerController extends Thread {
    private final static int PORT = 55000;
    public boolean listening = true;
    private static final ArrayList<ObjectOutputStream> oos = new ArrayList<>();

    public ServerController() {
        super("ServerController");
        start();
    }

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT , 0, InetAddress.getLocalHost());
            while (listening) {
                Socket socket = serverSocket.accept();
                ObjectOutputStream socketOOS = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream socketOIS = new ObjectInputStream(socket.getInputStream());
                synchronized (oos) { oos.add(socketOOS); }
                System.out.println("ServerThread " + oos.size());
                ServerThread readerThread = new ServerThread(oos, socketOIS);
                readerThread.start();
                ServerController.yield();
            }
        } catch (IOException e) {
            System.err.println("Не удается прослушать порт " + PORT);
        }
    }
}

