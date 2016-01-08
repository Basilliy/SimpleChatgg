package Basill;

import javax.swing.*;
import java.awt.*;

public class LittleInfo extends JFrame {
    public LittleInfo(){
        setSize(200, 70);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout());
        JLabel label = new JLabel("Жди подключения...");
        add(label);
    }
}
