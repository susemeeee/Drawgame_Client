/*
 * GamePage.java
 * Author : 박찬형
 * Created Date : 2020-11-15
 */
package UI.page;

import UI.ClientFrame;
import UI.page.element.CharacterArea;
import UI.page.element.ChatFrame;
import datatype.packet.Packet;
import datatype.packet.PacketType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GamePage extends Page {
    private final int MAX_USER = 8;

    private boolean isReady;
    private int userID;
    private boolean isAllReady;

    private ChatFrame chatFrame;
    private List<CharacterArea> characterAreas;
    private Canvas canvas;
    private JButton chatButton;
    private JButton quitButton;
    private JButton readyButton;
    private JButton startButton;

    public GamePage(){
        super();
        isReady = false;
        isAllReady = false;
        characterAreas = new ArrayList<>();
        setView();
    }

    @Override
    protected void initPage() {
        page.setSize(new Dimension(1500, 1000));
        page.setLocation(new Point(0, 0));
        page.setBackground(Color.WHITE);
        page.setLayout(null);
        page.setVisible(true);
    }

    public void responseUserData(int currentUser, int totalUser, int[] IDList, String[] names, ImageIcon[] icons,
                                 int yourID){
        for(int i = 0; i < totalUser; i++){
            page.add(characterAreas.get(i).getPanel());
        }
        page.repaint();

        for(int i = 0; i < IDList.length; i++){
            if(IDList[i] == 0){
                characterAreas.get(IDList[i]).setHostIcon();
            }
            characterAreas.get(IDList[i]).setUserName(names[IDList[i]]);
            characterAreas.get(IDList[i]).setUserIcon(icons[IDList[i]]);
        }
        page.repaint();

        userID = yourID;

        if(startButton != null){
            page.remove(startButton);
            startButton = null;
        }
        if(readyButton != null){
            page.remove(readyButton);
            readyButton = null;
        }
        if(userID == 0){
            generateStartButton();
        }
        else{
            generateReadyButton();
        }

        List<Integer> list = new ArrayList<>();
        for(int i : IDList){
            list.add(i);
        }
        for(int i = 0; i < MAX_USER; i++){
            if(!list.contains(i)){
                characterAreas.get(i).setUserName("");
                characterAreas.get(i).setUserIcon(new ImageIcon("files/defaultusericon.png"));
                characterAreas.get(i).setReadyStatusArea(false);
                characterAreas.get(i).getPanel().repaint();
            }
        }
        page.repaint();
    }

    private void ready(){
        Packet packet = new Packet(PacketType.READY);
        packet.addData("status", Boolean.toString(isReady));
        ClientFrame.getInstance().send(packet);
    }

    private void sendChat(){
        if(chatFrame.getMessageInputArea().getText().length() >= 1){
            Packet packet = new Packet(PacketType.CHAT);
            packet.addData("content", chatFrame.getMessageInputArea().getText());
            ClientFrame.getInstance().send(packet);
            chatFrame.getMessageInputArea().setText("");
        }
    }

    public void chatReceived(String sender, String content){
        if(chatFrame.getFrame() != null){
            chatFrame.appendChat(sender, content);
        }
    }

    public void start(){
        Packet packet = new Packet(PacketType.START_REQUEST);
        ClientFrame.getInstance().send(packet);
    }

    public void responseStartResult(boolean startAble){
        if(startAble){
            System.out.println("game start");
        }
        else{
            JOptionPane.showMessageDialog(null, "모두가 준비 되지 않았습니다.", "",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void readyStatusReceived(boolean[] readyStatusList, int[] IDList){
        for(int i = 0; i < MAX_USER; i++){
            characterAreas.get(IDList[i]).setReadyStatusArea(readyStatusList[i]);
        }
    }

    private void quitRoom(){
        ClientFrame.getInstance().quitRoom();
    }

    @Override
    protected void setView() {
        for(int i = 0; i < MAX_USER; i++){
            if(i % 2 == 0){
                characterAreas.add(new CharacterArea(50, 30 + 230 * (i / 2)));
            }
            else{
                characterAreas.add(new CharacterArea(1250, 30 + 230 * (i / 2)));
            }
        }

        JPanel canvasPanel = new JPanel();
        canvasPanel.setSize(new Dimension(800, 800));
        canvasPanel.setLocation(new Point(350, 50));
        canvasPanel.setLayout(new GridLayout());
        canvasPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        canvasPanel.setVisible(true);

        canvas = new Canvas();
        canvas.setSize(800, 800);
        canvas.setLocation(0, 0);
        canvas.setBackground(Color.WHITE);
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
            }
        });
        canvas.setVisible(true);
        canvasPanel.add(canvas);
        page.add(canvasPanel);

        chatButton = new JButton(new ImageIcon("files/chat.png"));
        chatButton.setSize(new Dimension(90, 70));
        chatButton.setLocation(new Point(350, 880));
        chatButton.setBackground(Color.ORANGE);
        chatButton.addActionListener(e -> {
            chatFrame = new ChatFrame(ClientFrame.getInstance().getFrame().getX(),
                    ClientFrame.getInstance().getFrame().getY());
            chatFrame.getMessageInputArea().addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    if(e.getKeyCode() == KeyEvent.VK_ENTER){
                        sendChat();
                    }
                }
            });
            chatFrame.getSendButton().addActionListener(e1 -> {
                sendChat();
            });
        });
        chatButton.setVisible(true);
        page.add(chatButton);

        quitButton = new JButton("나가기");
        quitButton.setSize(new Dimension(150, 70));
        quitButton.setLocation(new Point(750, 880));
        quitButton.setFont(new Font("SanSerif", Font.PLAIN, 24));
        quitButton.addActionListener(e -> {
            quitRoom();
        });
        quitButton.setVisible(true);
        page.add(quitButton);
    }

    private void generateStartButton(){
        startButton = new JButton("START");
        startButton.setSize(new Dimension(150, 70));
        startButton.setLocation(new Point(1000, 880));
        startButton.setFont(new Font("SanSerif", Font.PLAIN, 24));
        startButton.addActionListener(e -> {
            start();
        });
        startButton.setVisible(true);
        page.add(startButton);

        page.repaint();
    }

    private void generateReadyButton(){
        readyButton = new JButton("READY");
        readyButton.setSize(new Dimension(150, 70));
        readyButton.setLocation(new Point(1000, 880));
        readyButton.setFont(new Font("SanSerif", Font.PLAIN, 24));
        readyButton.addActionListener(e -> {
            isReady = !isReady;
            ready();
        });
        readyButton.setVisible(true);
        page.add(readyButton);

        page.repaint();
    }
}
