package forSnake;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
        System.out.println("Constructor RoomGUI");
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
//        run();

    }

    public void enterText() {
        if (!chatTextField.getText().equals("")) {
//            chatTextArea.setText(chatTextArea.getText() + name + ": " + chatTextField.getText() + "\n");
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
            System.out.println("Room1CreateStream");
            System.out.println("socket 3- " + in);
            out.writeObject("В комнату вошел игрок " + name + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                System.out.println("Room2ReadObject");
                Object object = in.readObject();
                System.out.println("Room3GetClass");
                if (object.getClass() == String.class) {
                    System.out.println("Room4CastToString");
                    String s = (String) object;
                        System.out.println("Room5IfEquals");
                        if (!s.equals("")) {
                            System.out.println("Room6SetText");
                            chatTextArea.setText(chatTextArea.getText() + s);
                            System.out.println("Room7GetScrollBar");
                            JScrollBar scrollBar = chatScroll.getVerticalScrollBar();
                            System.out.println("Room8ScrollBarSetValue");
                            scrollBar.setValue(scrollBar.getMaximum());
                        }
                }
                System.out.println("Room9ThreadYield");
                Thread.yield();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("Room10BreakWhile");
                break;
            }
        }
    }
}
