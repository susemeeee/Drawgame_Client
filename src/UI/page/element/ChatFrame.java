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
        frame.setLocation(new Point(x + 1500, y));
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setLayout(null);
        frame.setBackground(Color.WHITE);
        frame.setVisible(false);
    }

    private void setView(){
        JPanel chatPanel = new JPanel();
        chatPanel.setSize(new Dimension(400, 920));
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
        messageInputArea.setSize(new Dimension(320, 40));
        messageInputArea.setLocation(new Point(0, 920));
        messageInputArea.setFont(new Font("SanSerif", Font.PLAIN, 18));
        messageInputArea.setVisible(true);
        frame.add(messageInputArea);

        sendButton = new JButton(new ImageIcon("files/sendchat.png"));
        sendButton.setSize(new Dimension(60, 40));
        sendButton.setLocation(320, 920);
        sendButton.setFont(new Font("SanSerif", Font.BOLD, 24));
        sendButton.setVisible(true);
        frame.add(sendButton);
    }

    public void appendChat(String sender, String content){
        chatArea.append(sender + ": " + content + "\n");
        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
        frame.repaint();
    }

    public JTextField getMessageInputArea() {
        return messageInputArea;
    }

    public JButton getSendButton() {
        return sendButton;
    }

    public JFrame getFrame() {
        return frame;
    }
}
