/*
 * LoginPage.java
 * Author : 박찬형
 * Created Date : 2020-11-10
 */
package UI.page;

import UI.ClientFrame;
import datatype.User;

import javax.swing.*;
import java.awt.*;

public class LoginPage extends Page{
    private JTextField username;
    private JLabel characterIconArea;
    private ImageIcon characterIcon;
    private JButton selectIconButton;
    private JButton loginButton;

    public LoginPage(){
        super();
        setView();
    }

    @Override
    protected void initPage() {
        page.setSize(new Dimension(1500, 1000));
        page.setLocation(new Point(0, 0));
        page.setBackground(new Color(255, 255, 222));
        page.setLayout(null);
        page.setVisible(true);
    }

    @Override
    protected void setView() {
        loginButton = new JButton(new ImageIcon("files/loginbutton.png"));
        loginButton.setSize(new Dimension(400, 60));
        loginButton.setLocation(new Point(1000, 740));
        loginButton.setFont(new Font("SanSerif", Font.PLAIN, 24));
        loginButton.addActionListener(e -> {
            login();
        });
        loginButton.setVisible(true);
        page.add(loginButton);

        JLabel usernameText = new JLabel("username");
        usernameText.setSize(new Dimension(200, 60));
        usernameText.setLocation(new Point(750, 330));
        usernameText.setFont(new Font("SanSerif", Font.PLAIN, 30));
        usernameText.setHorizontalAlignment(JLabel.CENTER);
        usernameText.setVisible(true);
        page.add(usernameText);

        username = new JTextField();
        username.setSize(new Dimension(400, 60));
        username.setLocation(new Point(1000, 330));
        username.setFont(new Font("SanSerif", Font.PLAIN, 30));
        username.setVisible(true);
        page.add(username);

        JLabel characterText = new JLabel("character");
        characterText.setSize(new Dimension(200, 60));
        characterText.setLocation(new Point(750, 535));
        characterText.setFont(new Font("SanSerif", Font.PLAIN, 30));
        characterText.setHorizontalAlignment(JLabel.CENTER);
        characterText.setVisible(true);
        page.add(characterText);

        characterIcon = new ImageIcon("files/defaultusericon.png");

        characterIconArea = new JLabel(characterIcon);
        characterIconArea.setSize(new Dimension(250, 250));
        characterIconArea.setLocation(new Point(1000, 440));
        characterIconArea.setVisible(true);
        page.add(characterIconArea);

        selectIconButton = new JButton(new ImageIcon("files/choosebutton.png"));
        selectIconButton.setSize(new Dimension(50, 50));
        selectIconButton.setLocation(new Point(1350, 640));
        selectIconButton.setBackground(new Color(255, 255, 222));
        selectIconButton.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 222), 1));
        selectIconButton.addActionListener(e -> {
            selectCharacter();
        });
        selectIconButton.setVisible(true);
        page.add(selectIconButton);

        page.revalidate();
        page.repaint();
    }

    private void login(){
        if(username.getText().length() >= 1){
            ClientFrame.getInstance().connect();
            ClientFrame.getInstance().setUser(new User(username.getText(), characterIcon));
            ClientFrame.getInstance().login();
        }
        else{
            JOptionPane.showMessageDialog(null, "이름을 입력하세요.", "error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void selectCharacter(){
        FileDialog dialog = new FileDialog(ClientFrame.getInstance().getFrame(), "Select image", FileDialog.LOAD);
        dialog.setVisible(true);
        String directory = dialog.getDirectory();
        String name = dialog.getFile();
        if(directory != null && (name.endsWith(".png") || name.endsWith(".jpg"))){
            characterIcon = changeImageSize(new ImageIcon(directory + name));
            characterIconArea.setIcon(characterIcon);
        }
        else{
            JOptionPane.showMessageDialog(null, "확장자는 png, jpg만 가능합니다.", "error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private ImageIcon changeImageSize(ImageIcon icon){
        Image img = icon.getImage();
        Image newImg = img.getScaledInstance(250, 250, Image.SCALE_SMOOTH);
        ImageIcon newIcon = new ImageIcon(newImg);
        return newIcon;
    }
}
