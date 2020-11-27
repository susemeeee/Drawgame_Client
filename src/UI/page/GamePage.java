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

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class GamePage extends Page {
    private final int MAX_USER = 8;

    private boolean isReady;
    private int userID;
    private Color color;
    private int prevX;
    private int prevY;
    private int curX;
    private int curY;
    private int size;
    private BufferedImage bufferedImage;
    private Thread timer;
    private volatile boolean isTimerRun;
    private long roundTime;
    private volatile boolean running;

    private ChatFrame chatFrame;
    private List<CharacterArea> characterAreas;
    private Canvas canvas;
    private JButton chatButton;
    private JButton quitButton;
    private JButton readyButton;
    private JButton startButton;
    private JLabel gameNoticeArea;
    private JLabel timeArea;
    private JLabel answerArea;
    private JLabel canvasViewArea;
    private JPanel canvasPanel;

    private JPanel toolPanel;
    private JButton colorChangeButton;
    private JButton sizeUpButton;
    private JButton sizeDownButton;
    private JButton eraseButton;
    private JButton brushButton;

    public GamePage(){
        super();
        isReady = false;
        characterAreas = new ArrayList<>();
        setView();
        size = 5;
        color = Color.BLACK;
        isTimerRun = false;
        timer = null;
        running = false;
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
        Packet packet = new Packet(PacketType.READY);
        packet.addData("status", "request");
        ClientFrame.getInstance().send(packet);
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
        if(!startAble){
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

    public void startGame(){
        gameNoticeArea.setText("게임 시작");
        page.repaint();
    }

    public void startRound(int round, boolean isTestTaker, String word, long curTime){
        if(startButton != null){
            page.remove(startButton);
            startButton = null;
        }
        if(readyButton != null){
            page.remove(readyButton);
            readyButton = null;
        }
        if(quitButton != null){
            page.remove(quitButton);
            quitButton = null;
        }

        gameNoticeArea.setText(round + " 라운드");
        page.repaint();

        if(canvas != null){
            canvasPanel.remove(canvas);
            canvas = null;
        }
        if(canvasViewArea != null){
            canvasPanel.remove(canvasViewArea);
            canvasViewArea = null;
        }

        if(isTestTaker){
            answerArea.setText("정답: " + word);
            generateCanvas();
            generateToolPanel();
        }
        else{
            if(toolPanel != null){
                toolPanel.setVisible(false);
                page.remove(toolPanel);
                toolPanel = null;
            }
            answerArea.setText("정답: ?");
            generateCanvasView();
        }
        page.revalidate();
        page.repaint();

        roundTime = curTime;
        if(timer == null){
            timer = new Thread(() -> {
                while(isTimerRun){
                    running = true;
                    timeArea.setText("남은시간: " + (60 - ((System.currentTimeMillis() - roundTime) / 1000)) + "초");
                    try {
                        Thread.sleep(500);
                        if(System.currentTimeMillis() - curTime >= 60000){
                            timeArea.setText("남은시간: 0초");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally {
                        running = false;
                    }
                }
            });
            isTimerRun = true;
            timer.start();
        }
    }

    public void drawImageReceived(ImageIcon draw){
        if(canvasViewArea != null){
            canvasViewArea.setIcon(draw);
            page.repaint();
        }
    }

    public void endRound(String score){
        String resultText = "라운드 종료\n";
        if(score != null){
            resultText += ("정답자: " + score);
        }
        JOptionPane.showMessageDialog(null, resultText, "round result",
                JOptionPane.INFORMATION_MESSAGE);

        if(canvas != null){
            Graphics2D g = (Graphics2D) canvas.getGraphics();
            g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            g.dispose();
        }
        if(canvasViewArea != null){
            canvasViewArea.setIcon(null);
        }
        if(toolPanel != null){
            toolPanel.setVisible(false);
            page.remove(toolPanel);
            toolPanel = null;
        }
        page.revalidate();
        page.repaint();
    }

    public void endGame(List<String> names, List<Integer> scores){
        StringBuilder resultText = new StringBuilder("게임이 종료 되었습니다\n");
        for(int i = 0; i < names.size(); i++){
            resultText.append(i + "위: " + names.get(i) + "(" + scores.get(i) + " 점)\n");
        }
        JOptionPane.showMessageDialog(null, resultText.toString(), "game result",
                JOptionPane.INFORMATION_MESSAGE);

        Packet packet = new Packet(PacketType.REQUEST_USER);
        ClientFrame.getInstance().send(packet);

        isReady = false;
        packet = new Packet(PacketType.READY);
        packet.addData("status", "request");
        ClientFrame.getInstance().send(packet);

        quitButton = new JButton("나가기");
        quitButton.setSize(new Dimension(150, 70));
        quitButton.setLocation(new Point(800, 880));
        quitButton.setFont(new Font("SanSerif", Font.PLAIN, 24));
        quitButton.addActionListener(e -> {
            quitRoom();
        });
        quitButton.setVisible(true);
        page.add(quitButton);

        if(toolPanel != null){
            toolPanel.setVisible(false);
            page.remove(toolPanel);
            toolPanel = null;
        }
        answerArea.setText("");
        page.revalidate();
        page.repaint();

        isTimerRun = false;
        while (running) {
            Thread.onSpinWait();
        }
        timeArea.setText("");
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

        canvasPanel = new JPanel();
        canvasPanel.setSize(new Dimension(800, 800));
        canvasPanel.setLocation(new Point(350, 50));
        canvasPanel.setBackground(Color.WHITE);
        canvasPanel.setLayout(new GridLayout());
        canvasPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        canvasPanel.setVisible(true);

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
        quitButton.setLocation(new Point(800, 880));
        quitButton.setFont(new Font("SanSerif", Font.PLAIN, 24));
        quitButton.addActionListener(e -> {
            quitRoom();
        });
        quitButton.setVisible(true);
        page.add(quitButton);

        gameNoticeArea = new JLabel("게임 대기중");
        gameNoticeArea.setSize(new Dimension(200, 30));
        gameNoticeArea.setLocation(new Point(360, 10));
        gameNoticeArea.setFont(new Font("SanSerif", Font.PLAIN, 18));
        gameNoticeArea.setVisible(true);
        page.add(gameNoticeArea);

        timeArea = new JLabel();
        timeArea.setSize(new Dimension(200, 30));
        timeArea.setLocation(new Point(1010, 10));
        timeArea.setFont(new Font("SanSerif", Font.PLAIN, 18));
        timeArea.setVisible(true);
        page.add(timeArea);

        answerArea = new JLabel();
        answerArea.setSize(new Dimension(200, 30));
        answerArea.setLocation(new Point(750, 10));
        answerArea.setFont(new Font("SanSerif", Font.PLAIN, 18));
        answerArea.setVisible(true);
        page.add(answerArea);
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

    private void generateCanvas(){
        canvas = new Canvas();
        canvas.setSize(798, 798);
        canvas.setLocation(1, 1);
        canvas.setBackground(Color.WHITE);
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Graphics2D g = (Graphics2D)canvas.getGraphics();
                Graphics2D g2 = (Graphics2D) bufferedImage.getGraphics();
                g.setStroke(new BasicStroke(size));
                g.setColor(color);
                curX = e.getX();
                curY = e.getY();
                g.drawLine(prevX, prevY, curX, curY);

                g2.setStroke(new BasicStroke(size));
                g2.setColor(color);
                g2.drawLine(prevX, prevY, curX, curY);

                prevX = curX;
                prevY = curY;
                g.dispose();
                g2.dispose();
                page.repaint();

                Packet packet = new Packet(PacketType.DRAW);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                try {
                    ImageIO.write(bufferedImage, "png", out);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                packet.addData("image", Base64.getEncoder().encodeToString(out.toByteArray()));
                ClientFrame.getInstance().send(packet);
            }
        });
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Graphics2D g = (Graphics2D)canvas.getGraphics();
                Graphics2D g2 = (Graphics2D)bufferedImage.getGraphics();
                g.setColor(color);
                g.fillOval(e.getX(), e.getY(), size, size);
                g2.setColor(color);
                g2.fillOval(e.getX(), e.getY(), size, size);
                g.dispose();
                g2.dispose();
                page.repaint();

                Packet packet = new Packet(PacketType.DRAW);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                try {
                    ImageIO.write(bufferedImage, "png", out);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                packet.addData("image", Base64.getEncoder().encodeToString(out.toByteArray()));
                ClientFrame.getInstance().send(packet);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                prevX = e.getX();
                prevY = e.getY();
                curX = prevX;
                curY = prevY;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Packet packet = new Packet(PacketType.DRAW);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                try {
                    ImageIO.write(bufferedImage, "png", out);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                packet.addData("image", Base64.getEncoder().encodeToString(out.toByteArray()));
                ClientFrame.getInstance().send(packet);
            }
        });
        canvasPanel.add(canvas);
        canvas.setVisible(true);
        canvasPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        canvasPanel.repaint();
        page.repaint();

        bufferedImage = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = (Graphics2D) bufferedImage.getGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        g2.dispose();
    }

    private void generateCanvasView(){
        canvasViewArea = new JLabel();
        canvasViewArea.setSize(new Dimension(798, 798));
        canvasViewArea.setLocation(new Point(1, 1));
        canvasViewArea.setBackground(Color.WHITE);
        canvasViewArea.setVisible(true);
        canvasPanel.add(canvasViewArea);
        canvasPanel.repaint();
    }

    private void generateToolPanel(){
        toolPanel = new JPanel();
        toolPanel.setSize(new Dimension(320, 70));
        toolPanel.setLocation(new Point(450, 880));
        toolPanel.setBackground(new Color(222, 239, 255));
        toolPanel.setLayout(null);
        toolPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        toolPanel.setVisible(true);

        Dimension buttonSize = new Dimension(50, 50);

        brushButton = new JButton(new ImageIcon("files/brushbutton.png"));
        brushButton.setSize(buttonSize);
        brushButton.setLocation(new Point(15, 10));
        brushButton.addActionListener(e -> {
            color = Color.BLACK;
        });
        brushButton.setVisible(true);
        toolPanel.add(brushButton);

        eraseButton = new JButton(new ImageIcon("files/erasebutton.png"));
        eraseButton.setSize(buttonSize);
        eraseButton.setLocation(new Point(75, 10));
        eraseButton.addActionListener(e -> {
            color = Color.WHITE;
        });
        eraseButton.setBackground(Color.WHITE);
        eraseButton.setVisible(true);
        toolPanel.add(eraseButton);

        colorChangeButton = new JButton(new ImageIcon("files/colorchangebutton.png"));
        colorChangeButton.setSize(buttonSize);
        colorChangeButton.setLocation(new Point(135, 10));
        colorChangeButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(null, "change color", Color.BLACK);
            if(newColor != null){
                color = newColor;
            }
        });
        colorChangeButton.setBackground(Color.WHITE);
        colorChangeButton.setVisible(true);
        toolPanel.add(colorChangeButton);

        sizeUpButton = new JButton(new ImageIcon("files/plusbutton.png"));
        sizeUpButton.setSize(buttonSize);
        sizeUpButton.setLocation(new Point(195, 10));
        sizeUpButton.addActionListener(e -> {
            if(size < 29){
                size += 2;
            }
        });
        sizeUpButton.setBackground(Color.WHITE);
        sizeUpButton.setVisible(true);
        toolPanel.add(sizeUpButton);

        sizeDownButton = new JButton(new ImageIcon("files/minusbutton.png"));
        sizeDownButton.setSize(buttonSize);
        sizeDownButton.setLocation(new Point(255, 10));
        sizeDownButton.addActionListener(e -> {
            if(size > 1){
                size -= 2;
            }
        });
        sizeDownButton.setBackground(Color.WHITE);
        sizeDownButton.setVisible(true);
        toolPanel.add(sizeDownButton);

        page.add(toolPanel);
        page.repaint();
    }
}
