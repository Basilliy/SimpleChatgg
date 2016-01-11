package forSnake;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class SelectRoomGUI extends JFrame{
    private JPanel mainPanel;
    private JButton buttonConnectToRoom;
    private JButton buttonAddRoom;
    private JButton buttonCreateRoom;
    private JButton buttonDeleteRoom;
    private JButton buttonUpdateRooms;
    private JList<Object> listRoom;
    private JTextField textField;

    private File rooms = new File("src/Data/Servers.txt");
    private ArrayList<ListItem> list;
    private final static int PORT = 55000;


    public SelectRoomGUI() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(400, 315));
        setSize(new Dimension(400, 400));
        setContentPane(mainPanel);
        setVisible(true);

        readRooms();


        buttonAddRoom.addActionListener(e -> {
            writeRoom(JOptionPane.showInputDialog(SelectRoomGUI.this, "Введи ip-адресс или название сети",
                    "Добавление комнаты", JOptionPane.PLAIN_MESSAGE));
            readRooms();
        });

        buttonUpdateRooms.addActionListener(e -> updateRooms());

        buttonConnectToRoom.addActionListener(e -> {
            if (listRoom.getSelectedIndex() >= 0)
                startLikeClient((ListItem) listRoom.getSelectedValue(),textField.getText());
        });

        buttonCreateRoom.addActionListener(e -> startLikeServer(textField.getText()));

        buttonDeleteRoom.addActionListener(e -> deleteRoom(listRoom.getSelectedIndex()));
    }

    private void writeRoom(String ip){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(rooms, true));
            writer.write(ip);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            try {
                boolean b = rooms.createNewFile();
                if (!b) System.out.println("Отсутствует путь src/Data");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private void readRooms() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(rooms));
            list = new ArrayList<>();
            while (reader.ready()) {
                String s = reader.readLine();
                if (s.length() > 1)
                list.add(new ListItem(s));
            }
            reader.close();

            listRoom.setListData(list.toArray());

            updateRooms();
        } catch (FileNotFoundException e) {
            try {
                if (!rooms.createNewFile()) System.out.println("Отсутствует путь src/Data");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateRooms() {
        for (ListItem item : list) {
            item.isConnected = false;
            item.name = null;
        }
        listRoom.setListData(list.toArray());

        for (ListItem item : list) {
            try {
                InetAddress inetAddress = InetAddress.getByName(item.ip);
                if (inetAddress.isReachable(1500)) {
                    item.isConnected = true;
                    item.name = inetAddress.getCanonicalHostName();
                } else {
                    item.isConnected = false;
                    item.name = inetAddress.getCanonicalHostName();
                }
            } catch (Exception e) {
                item.isConnected = false;
                e.printStackTrace();
            }
            listRoom.setListData(list.toArray());
        }
    }

    private void deleteRoom(int index) {
        list.remove(index);
        listRoom.setListData(list.toArray());
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(rooms));
            for (ListItem item: list) {
                writer.write(item.ip);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startLikeClient(ListItem item, String name) {
        if (!name.equals("")) {
            try {
                Socket socket = new Socket(InetAddress.getByName(item.ip), PORT);
                if (socket.isConnected()) {
                    this.setVisible(false);
                    new RoomGUI(socket, name, SelectRoomGUI.this);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(SelectRoomGUI.this, "Введите имя");
        }
    }

    private void startLikeServer(String name) {
        if (!name.equals("")) {
            try {
                new ServerController();
                startLikeClient(new ListItem(InetAddress.getLocalHost().getHostAddress()),name);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(SelectRoomGUI.this, "Введите имя");
        }
    }



    private class ListItem {
        public String name;
        public String ip;
        public boolean isConnected;

        public ListItem(String ip) {
            this.ip = ip;
        }

        @Override
        public String toString() {
            if (name == null) return ip;
            if (name.equals(ip))
                if (isConnected) return ip + " : Доступно";
                else return ip + " : Недоступно";
            if (isConnected) return name + " : " + ip + " : Доступно";
            else return ip + " : Недоступно";
        }
    }

}
