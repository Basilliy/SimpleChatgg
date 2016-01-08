package Basill;

import javax.swing.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Main{
    public Socket socket;
    private static final int PORT = 55000;

    public Main(){
        ServerSocket server;

        try {
            String name = JOptionPane.showInputDialog("Твое имя?" +
                    "\nLocal host name: " +  InetAddress.getLocalHost().getHostName() +
                    "\nLocal host IP address: " + InetAddress.getLocalHost().getHostAddress());

            int i = JOptionPane.showConfirmDialog(null, "Ты сервер?");
            //0 - Сервер
            //1 - Клиент
            //2 - Выход
            if (i == 2) System.exit(1);
            if (i == 0) { // Для Сервера
                server = new ServerSocket(PORT,0,InetAddress.getLocalHost());
                LittleInfo littleInfo = new LittleInfo();
                littleInfo.setVisible(true);
                socket = server.accept();
                littleInfo.setVisible(false);
                new Client("Сервер", socket, name);
            } else
            if (i == 1){ // Для клиента
                String sIP = JOptionPane.showInputDialog("Введи IP сервера");
                socket = new Socket(InetAddress.getByName(sIP), PORT);
                new Client("Клиент", socket, name);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Хуйня с сокетом");
            e.printStackTrace();
            System.exit(2);
        }
    }

    public static void main(String[] args) {
        new Main();
    }
}
