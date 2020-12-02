/*
 * CharacterArea.java
 * Author : 박찬형
 * Created Date : 2020-11-15
 */
package UI.page.element;

import javax.swing.*;
import java.awt.*;

public class CharacterArea {
    private JPanel panel;
    private JLabel characterIconArea;
    private JLabel userNameArea;
    private JLabel readyStatusArea;
    private JLabel hostIconArea;

    public CharacterArea(int x, int y){
        initPanel(x, y);
        setView();
    }

    private void initPanel(int x, int y){
        panel = new JPanel();
        panel.setSize(new Dimension(200, 210));
        panel.setLocation(x, y);
        panel.setBackground(new Color(222, 239, 255));
        panel.setLayout(null);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        panel.setVisible(true);
    }

    public void setHostIcon(){
        hostIconArea.setIcon(changeImageSize(new ImageIcon("files/host.png"), 70, 70));
        hostIconArea.setHorizontalAlignment(JLabel.CENTER);
        readyStatusArea.setText("");
        panel.repaint();
    }

    public void setUserName(String name){
        userNameArea.setText(name);
        panel.repaint();
    }

    public void setUserIcon(ImageIcon icon){
        characterIconArea.setIcon(changeImageSize(icon, 130, 130));
        panel.repaint();
    }

    public void setReadyStatusArea(boolean isReady){
        if(isReady){
            readyStatusArea.setForeground(Color.GREEN);
        }
        else{
            readyStatusArea.setForeground(Color.RED);
        }
    }

    private void setView(){
        characterIconArea = new JLabel();
        characterIconArea.setSize(new Dimension(130, 130));
        characterIconArea.setLocation(new Point(1, 1));
        characterIconArea.setVisible(true);
        panel.add(characterIconArea);

        hostIconArea = new JLabel();
        hostIconArea.setSize(new Dimension(70, 120));
        hostIconArea.setLocation(new Point(130, 0));
        hostIconArea.setHorizontalAlignment(JLabel.CENTER);
        hostIconArea.setVisible(true);
        panel.add(hostIconArea);

        userNameArea = new JLabel("");
        userNameArea.setSize(new Dimension(200, 40));
        userNameArea.setLocation(new Point(0, 130));
        userNameArea.setFont(new Font("SanSerif", Font.PLAIN, 18));
        userNameArea.setHorizontalAlignment(JLabel.CENTER);
        userNameArea.setVisible(true);
        panel.add(userNameArea);

        readyStatusArea = new JLabel("READY");
        readyStatusArea.setSize(new Dimension(200, 40));
        readyStatusArea.setLocation(new Point(0, 170));
        readyStatusArea.setFont(new Font("SanSerif", Font.PLAIN, 18));
        readyStatusArea.setForeground(Color.RED);
        readyStatusArea.setHorizontalAlignment(JLabel.CENTER);
        readyStatusArea.setVisible(true);
        panel.add(readyStatusArea);
    }

    private ImageIcon changeImageSize(ImageIcon icon, int width, int height){
        Image img = icon.getImage();
        Image newImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(newImg);
    }

    public JPanel getPanel() {
        return panel;
    }
}
