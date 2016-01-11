package forSnake;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerController extends Thread {
    private final static int PORT = 55000;
    public boolean listening = true;
    private final ArrayList<Socket> sockets = new ArrayList<>();

    PipedInputStream pIn = new PipedInputStream();

    public ServerController() {
        super("ServerController");
        (new Accepter()).start();
        start();
    }

    public void run() {
        while (listening){
            ArrayList<Socket> socketsClone;
            synchronized (sockets) {
                socketsClone = new ArrayList<>(sockets);
            }
            try {
                if (pIn.available() > 0) {
                    ObjectInputStream inS = new ObjectInputStream(pIn);
                    Object o = inS.readObject();
                    for (Socket s : socketsClone) {
                        ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
                        out.writeObject(o);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private class Accepter extends Thread {
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(PORT , 0, InetAddress.getLocalHost());
                while (listening) {
                    Socket socket = serverSocket.accept();
                    synchronized (sockets) {
                        sockets.add(socket);
                        System.out.println("ServerThread " + sockets.size());
                        ReaderByServerThread readerThread = new ReaderByServerThread(socket, pIn);
                        readerThread.run();
                        System.out.println("added new thread to ServerThread");
                    }
                }
            } catch (IOException e) {
                System.err.println("Не удается прослушать порт " + PORT);
            }
        }
    }
}

