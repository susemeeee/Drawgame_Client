/*
 * ClientFrame.java
 * Author : 박찬형
 * Created Date : 2020-11-10
 */
package UI;

import UI.page.*;
import datatype.User;
import datatype.packet.Packet;
import datatype.packet.PacketType;
import net.Connection;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.EnumMap;

public class ClientFrame {
    private Connection connection;
    private User user;

    private JFrame frame;
    private EnumMap<PageType, Page> pages;
    private PageType currentPage;

    private ClientFrame(){
        connection = new Connection();
        frame = new JFrame("Drawing Game");
        initFrame();
        pages = new EnumMap<>(PageType.class);
        addPages();
        switchPage(PageType.LOGIN);
    }

    private void initFrame(){
        frame.setSize(new Dimension(1500, 1000));
        frame.setResizable(false);
        frame.setLayout(new GridLayout(1, 1));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void addPages(){
        pages.put(PageType.LOGIN, new LoginPage());
        pages.put(PageType.MAIN_PAGE, new MainPage());
        pages.put(PageType.GAME_PAGE, new GamePage());
    }

    public void resetPage(PageType type){
        Page newPage = null;
        if(type == PageType.LOGIN){
            newPage = new LoginPage();
        }
        else if(type == PageType.MAIN_PAGE){
            newPage = new MainPage();
        }

        if(newPage != null){
            pages.replace(type, newPage);
        }
    }

    public void switchPage(PageType type){
        frame.getContentPane().removeAll();
        frame.add(pages.get(type).getPanel());
        frame.revalidate();
        frame.repaint();
        currentPage = type;
    }

    public void connect(){
        connection.connect("localhost", 9002);
    }

    public void login(){
        if(user == null){
            return;
        }

        Packet packet = new Packet(PacketType.LOGIN);
        packet.addData("name", user.getName());
        ImageIcon icon = user.getCharacterIcon();
        Image img = icon.getImage();
        BufferedImage bufferedImage = new BufferedImage(img.getWidth(null), img.getHeight(null),
                BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Graphics2D g;
            g = bufferedImage.createGraphics();
            icon.paintIcon(null, g, 0, 0);
            ImageIO.write(bufferedImage, "png", out);
            g.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
        packet.addData("characterIcon", Base64.getEncoder().encodeToString(out.toByteArray()));
        connection.send(packet);

        switchPage(PageType.MAIN_PAGE);
        ((MainPage)pages.get(PageType.MAIN_PAGE)).requestRoomData();
    }

    public void responseRoomData(int totalRoomCount, String[] roomNames,
                                 String[] roomMaxPerson, String[] roomCurrentPerson, int[] roomIDList){
        ((MainPage)pages.get(PageType.MAIN_PAGE)).responseRoomData(totalRoomCount, roomNames,
                roomMaxPerson, roomCurrentPerson, roomIDList);
    }

    public void responseUserData(int currentUser, int totalUser, int[] IDList, String[] names, ImageIcon[] icons,
                                 int yourID){
        ((GamePage)pages.get(PageType.GAME_PAGE)).responseUserData(currentUser, totalUser, IDList, names, icons,
                yourID);
    }

    public void responseJoinRoomResult(String result){
        ((MainPage)pages.get(PageType.MAIN_PAGE)).responseJoinRoomResult(result);
    }

    public void chatReceived(String sender, String content){
        ((GamePage)pages.get(PageType.GAME_PAGE)).chatReceived(sender, content);
    }

    public void readyStatusReceived(boolean[] readyStatusList, int[] idList){
        ((GamePage)pages.get(PageType.GAME_PAGE)).readyStatusReceived(readyStatusList, idList);
    }

    public void responseStartResult(boolean startAble){
        ((GamePage)pages.get(PageType.GAME_PAGE)).responseStartResult(startAble);
    }

    public void send(Packet packet){
        connection.send(packet);
    }

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }

    public JFrame getFrame(){
        return frame;
    }

    public static ClientFrame getInstance(){
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder{
        private static final ClientFrame INSTANCE = new ClientFrame();
    }
}
