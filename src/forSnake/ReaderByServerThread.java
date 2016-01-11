package forSnake;

import java.io.*;
import java.net.Socket;

public class ReaderByServerThread implements Runnable {

    private Socket client;
    private boolean listening = true;
    public PipedInputStream pipeIn;

    public ReaderByServerThread(Socket client, PipedInputStream pipeIn){
        this.client = client;
        this.pipeIn = pipeIn;
    }

    @Override
    public void run() {
        while (listening) {
            try {
                ObjectInputStream inC = new ObjectInputStream(client.getInputStream());
                PipedOutputStream pipeOut = new PipedOutputStream(pipeIn);
                ObjectOutputStream outS = new ObjectOutputStream(pipeOut);
                outS.writeObject(inC.readObject());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
