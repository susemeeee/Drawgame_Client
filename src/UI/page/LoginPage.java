/*
 * LoginPage.java
 * Author : 박찬형
 * Created Date : 2020-11-10
 */
package UI.page;

import javax.swing.*;
import java.awt.*;

public class LoginPage extends Page{
    private JTextField username;
    private ImageIcon characterIcon;
    private JButton addIconButton;
    private JButton loginButton;

    public LoginPage(){
        page = new JPanel();
        initPage();
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

    @Override
    protected void setView() {
        loginButton = new JButton("LOGIN");
        loginButton.setSize(new Dimension(400, 60));
        loginButton.setLocation(new Point(1000, 740));
        loginButton.addActionListener(e -> {
            //TODO 로그인
            System.out.println("login");
        });
        loginButton.setVisible(true);
        page.add(loginButton);
    }
}
