/*
 * ChatFrame.java
 * Author : 박찬형
 * Created Date : 2020-11-15
 */
package UI.page.element;

import javax.swing.*;
import java.awt.*;

public class ChatFrame {
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageInputArea;
    private JButton sendButton;
    private JScrollPane scrollPane;

    public ChatFrame(int x, int y){
        initFrame(x, y);
        setView();
    }

    private void initFrame(int x, int y){
        frame = new JFrame("chat");
        frame.setSize(new Dimension(400, 1000));
        frame.setLocation(new Point(x + 10, y + 10));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);
        frame.setBackground(Color.WHITE);
        frame.setVisible(true);
    }

    private void setView(){
        JPanel chatPanel = new JPanel();
        chatPanel.setSize(new Dimension(400, 940));
        chatPanel.setLocation(new Point(0, 0));
        chatPanel.setLayout(new GridLayout());
        chatPanel.setVisible(true);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        scrollPane = new JScrollPane(chatArea);
        scrollPane.setVisible(true);
        chatArea.setVisible(true);
        chatPanel.add(scrollPane);
        frame.add(chatPanel);

        messageInputArea = new JTextField();
        messageInputArea.setSize(new Dimension(340, 60));
        messageInputArea.setLocation(new Point(0, 940));
        messageInputArea.setFont(new Font("SanSerif", Font.PLAIN, 18));
        messageInputArea.setVisible(true);
        frame.add(messageInputArea);

        sendButton = new JButton(">");
        sendButton.setSize(new Dimension(60, 60));
        sendButton.setLocation(340, 940);
        sendButton.setFont(new Font("SanSerif", Font.BOLD, 24));
        sendButton.addActionListener(e -> {

        });
        sendButton.setVisible(true);
        frame.add(sendButton);
    }
}
