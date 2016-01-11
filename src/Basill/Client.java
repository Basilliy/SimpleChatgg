package Basill;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class Client extends JFrame implements Runnable{
    private JPanel mainPanel;
    private JTextPane chatText;
    private JTextPane enterText;
    private JButton button;
    private JScrollPane scroll;
    private Socket socket;
    private String name;

    public Client(String type, Socket socket, String name){
        this.socket = socket;
        this.name = name;
        setSize(300, 400);
        setTitle("Чат - " + type + " - " + name);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(mainPanel);

        button.addActionListener(e -> {
            enterText();
            enterText.setFocusable(true);
        });
        enterText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    enterText();
            }
        });

        setVisible(true);
        run();
    }

    public void enterText(){
        chatText.setText(chatText.getText() + "\n" + name + ": " + enterText.getText());
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(name + ": " + enterText.getText());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Хуйня с Ввод/Вывод (out)\n" +
                    " Ну или сервер отключился");
            e.printStackTrace();
            System.exit(2);
        }
        JScrollBar scrollBar = scroll.getVerticalScrollBar();
        scrollBar.setValue(scrollBar.getMaximum());
        enterText.setText(null);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                String s = (String) in.readObject();
                if (s != null && !s.isEmpty()) {
                    chatText.setText(chatText.getText() + "\n" + s);
                    JScrollBar scrollBar = scroll.getVerticalScrollBar();
                    scrollBar.setValue(scrollBar.getMaximum());
                }
                Thread.yield();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Хуйня с Ввод/Вывод (in)\n Ну или сервер отключился");
                e.printStackTrace();
                System.exit(2);
            } catch (ClassNotFoundException e) {
                JOptionPane.showMessageDialog(null, "Хуйня с Кастом String");
                e.printStackTrace();
                System.exit(2);
            }
        }
    }


}
