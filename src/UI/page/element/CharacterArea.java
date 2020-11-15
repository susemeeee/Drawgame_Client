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
        panel.setBackground(Color.WHITE);
        panel.setLayout(null);
        panel.setVisible(true);
    }

    private void setView(){
        //TODO 테스트용으로 데이터 넣어놓은 거 변경
        characterIconArea = new JLabel(changeImageSize(new ImageIcon("files/defaultusericon.png"),
                130, 130));
        characterIconArea.setSize(new Dimension(130, 130));
        characterIconArea.setLocation(new Point(0, 0));
        characterIconArea.setVisible(true);
        panel.add(characterIconArea);

        hostIconArea = new JLabel(changeImageSize(new ImageIcon("files/host.png"), 70, 70));
        hostIconArea.setSize(new Dimension(70, 120));
        hostIconArea.setLocation(new Point(130, 0));
        hostIconArea.setHorizontalAlignment(JLabel.CENTER);
        hostIconArea.setVisible(true);
        panel.add(hostIconArea);

        userNameArea = new JLabel("username");
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
        readyStatusArea.setHorizontalAlignment(JLabel.CENTER);
        readyStatusArea.setVisible(true);
        panel.add(readyStatusArea);
    }

    private ImageIcon changeImageSize(ImageIcon icon, int width, int height){
        Image img = icon.getImage();
        Image newImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon newIcon = new ImageIcon(newImg);
        return newIcon;
    }

    public JPanel getPanel() {
        return panel;
    }
}