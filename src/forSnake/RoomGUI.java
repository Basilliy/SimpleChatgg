package forSnake;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class RoomGUI extends JFrame implements Runnable {
    private JPanel mainPanel;
    private JPanel nastyaPanel;
    private JPanel chatPanel;
    public  JTextArea chatTextArea;
    private JButton chatButtonEnter;
    private JTextField chatTextField;
    public JScrollPane chatScroll;

    public Socket socket;
    private String name;
    ObjectOutputStream out;
    ObjectInputStream in;


    public RoomGUI(Socket socket, String name, JFrame owner) {
        this.name = name;
        this.socket = socket;
        setTitle(name);
        setContentPane(mainPanel);
        setSize(450, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(owner);

        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        chatButtonEnter.addActionListener(e -> {
            enterText();
            chatTextField.setFocusable(true);
        });
        chatTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    enterText();
            }
        });
        setVisible(true);
    }

    public void enterText() {
        if (!chatTextField.getText().equals("")) {
            try {
                out.writeObject(name + ": " + chatTextField.getText() + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            JScrollBar scrollBar = chatScroll.getVerticalScrollBar();
            scrollBar.setValue(scrollBar.getMaximum());
            chatTextField.setText("");
        }
    }

    @Override
    public void run() {
        try {
            out.writeObject("В комнату вошел игрок " + name + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                Object object = in.readObject();
                if (object.getClass() == String.class) {
                    String s = (String) object;
                        if (!s.equals("")) {
                            chatTextArea.setText(chatTextArea.getText() + s);
                            JScrollBar scrollBar = chatScroll.getVerticalScrollBar();
                            scrollBar.setValue(scrollBar.getMaximum());
                        }
                }
                Thread.yield();
            } catch (Exception se) {
                JOptionPane.showMessageDialog(RoomGUI.this, "Сервер отключился");
                break;
            }
        }
    }
}
