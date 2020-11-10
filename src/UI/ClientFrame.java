/*
 * ClientFrame.java
 * Author : 박찬형
 * Created Date : 2020-11-10
 */
package UI;

import UI.page.LoginPage;
import UI.page.Page;
import UI.page.Pagetype;
import net.Connection;

import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;

public class ClientFrame {
    private Connection connection;

    private JFrame frame;
    private EnumMap<Pagetype, Page> pages;
    private Pagetype currentPage;

    private ClientFrame(){
        connection = new Connection();
        frame = new JFrame();
        initFrame();
        pages = new EnumMap<>(Pagetype.class);
        addPages();
        switchPage(Pagetype.LOGIN);
    }

    private void initFrame(){
        frame.setSize(new Dimension(1500, 1000));
        frame.setResizable(false);
        frame.setLayout(new GridLayout(1, 1));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void addPages(){
        pages.put(Pagetype.LOGIN, new LoginPage());
    }

    public void resetPage(Pagetype type){
        Page newPage = null;
        if(type == Pagetype.LOGIN){
            newPage = new LoginPage();
        }

        if(newPage != null){
            pages.replace(type, newPage);
        }
    }

    public void switchPage(Pagetype type){
        frame.getContentPane().removeAll();
        frame.add(pages.get(type).getPanel());
        frame.revalidate();
        frame.repaint();
        currentPage = type;
    }

    public void connect(){
        connection.connect("localhost", 9002);
    }

    public static ClientFrame getInstance(){
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder{
        private static final ClientFrame INSTANCE = new ClientFrame();
    }
}
