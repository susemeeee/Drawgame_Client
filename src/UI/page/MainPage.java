/*
 * MainPage.java
 * Author : 박찬형
 * Created Date : 2020-11-11
 */
package UI.page;

import UI.ClientFrame;
import datatype.packet.Packet;
import datatype.packet.PacketType;

import javax.swing.*;
import java.awt.*;

public class MainPage extends Page {
    private int prevPageNumber;
    private int pageNumber;
    private int currentPageRoomCount;

    private JList<String> roomListView;
    private JList<String> parsonCountListView;
    private JButton prevPageButton;
    private JButton nextPageButton;
    private JLabel pageNumberText;
    private JButton joinRoomButton;
    private JTextField roomName;
    private JComboBox<String> roomParson;
    private JComboBox<String> round;
    private JButton makeRoomButton;

    public MainPage(){
        super();
        pageNumber = 1;
        prevPageNumber = 1;
        currentPageRoomCount = 0;
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

    public void requestRoomData(){
        Packet packet = new Packet(PacketType.REQUEST_ROOM);
        packet.addData("page", Integer.toString(pageNumber));
        ClientFrame.getInstance().send(packet);
    }

    public void responseRoomData(int totalRoomCount, String[] roomNames,
                                 String[] roomMaxPerson, String[] roomCurrentPerson){
        if(pageNumber != 1 && totalRoomCount == 0){
            pageNumber = prevPageNumber;
            return;
        }
        else if(pageNumber == 1 && totalRoomCount == 0){
            currentPageRoomCount = totalRoomCount;
            String[] data = new String[]{"", "", "", "", "", "", "", "", "", ""};
            roomListView.setListData(data);
            parsonCountListView.setListData(data);
            page.repaint();
            return;
        }
        currentPageRoomCount = totalRoomCount;
        String[] roomData = new String[10];
        String[] parsonData = new String[10];
        for(int i = 0; i < 10; i++){
            if(i < totalRoomCount){
                roomData[i] = " " + roomNames[i];
                parsonData[i] = " " + roomCurrentPerson[i] + " / " + roomMaxPerson[i];
            }
            else{
                roomData[i] = "";
                parsonData[i] = "";
            }
        }
        roomListView.clearSelection();
        parsonCountListView.clearSelection();
        roomListView.setListData(roomData);
        parsonCountListView.setListData(parsonData);

        pageNumberText.setText(Integer.toString(pageNumber));

        page.revalidate();
        page.repaint();
    }

    public void makeRoom(){
        if(roomName.getText().length() < 1){
            JOptionPane.showMessageDialog(null, "방 이름을 입력하세요.", "error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        Packet packet = new Packet(PacketType.MAKE_ROOM);
        packet.addData("roomname", roomName.getText());
        packet.addData("maxperson", (String)roomParson.getSelectedItem());
        packet.addData("maxround", (String)round.getSelectedItem());
        ClientFrame.getInstance().send(packet);
    }

    @Override
    protected void setView() {
        roomListView = new JList<>();
        roomListView.setSize(new Dimension(700, 750));
        roomListView.setLocation(new Point(100, 100));
        roomListView.setFont(new Font("SanSerif", Font.PLAIN, 28));
        roomListView.setFixedCellHeight(roomListView.getHeight() / 10);
        roomListView.addListSelectionListener(e -> {
            if(roomListView.getSelectedIndex() >= currentPageRoomCount){
                roomListView.clearSelection();
                parsonCountListView.clearSelection();
                page.repaint();
                return;
            }
            parsonCountListView.setSelectedIndex(roomListView.getSelectedIndex());
            page.repaint();
        });
        roomListView.setCellRenderer(new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                JLabel listCellRenderer = (JLabel)super.getListCellRendererComponent(list, value, index,
                        isSelected, cellHasFocus);
                listCellRenderer.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
                return listCellRenderer;
            }
        });
        roomListView.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, Color.BLACK));
        roomListView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomListView.setVisible(true);
        page.add(roomListView);

        parsonCountListView = new JList<>();
        parsonCountListView.setSize(new Dimension(100, 750));
        parsonCountListView.setLocation(new Point(800, 100));
        parsonCountListView.setFont(new Font("SanSerif", Font.PLAIN, 28));
        parsonCountListView.setFixedCellHeight(parsonCountListView.getHeight() / 10);
        parsonCountListView.addListSelectionListener(e -> {
            if(parsonCountListView.getSelectedIndex() >= currentPageRoomCount){
                roomListView.clearSelection();
                parsonCountListView.clearSelection();
                page.repaint();
                return;
            }
            roomListView.setSelectedIndex(parsonCountListView.getSelectedIndex());
            page.repaint();
        });
        parsonCountListView.setCellRenderer(new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                JLabel listCellRenderer = (JLabel)super.getListCellRendererComponent(list, value, index,
                        isSelected, cellHasFocus);
                listCellRenderer.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
                return listCellRenderer;
            }
        });
        parsonCountListView.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Color.BLACK));
        parsonCountListView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        parsonCountListView.setVisible(true);
        page.add(parsonCountListView);

        pageNumberText = new JLabel(Integer.toString(pageNumber));
        pageNumberText.setSize(new Dimension(100, 50));
        pageNumberText.setLocation(new Point(450, 880));
        pageNumberText.setHorizontalAlignment(JLabel.CENTER);
        pageNumberText.setFont(new Font("SanSerif", Font.PLAIN, 28));
        pageNumberText.setVisible(true);
        page.add(pageNumberText);

        prevPageButton = new JButton("<");
        prevPageButton.setSize(new Dimension(80, 50));
        prevPageButton.setLocation(new Point(320, 880));
        prevPageButton.addActionListener(e -> {
            if(pageNumber > 1){
                prevPageNumber = pageNumber;
                pageNumber--;
            }
            requestRoomData();
        });
        prevPageButton.setFont(new Font("SanSerif", Font.PLAIN, 28));
        prevPageButton.setVisible(true);
        page.add(prevPageButton);

        nextPageButton = new JButton(">");
        nextPageButton.setSize(new Dimension(80, 50));
        nextPageButton.setLocation(new Point(600, 880));
        nextPageButton.addActionListener(e -> {
            prevPageNumber = pageNumber;
            pageNumber++;
            requestRoomData();
        });
        nextPageButton.setFont(new Font("SanSerif", Font.PLAIN, 28));
        nextPageButton.setVisible(true);
        page.add(nextPageButton);

        JPanel makeRoomPanel = new JPanel();
        makeRoomPanel.setSize(new Dimension(400, 620));
        makeRoomPanel.setLocation(new Point(1000, 100));
        makeRoomPanel.setBackground(Color.WHITE);
        makeRoomPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        makeRoomPanel.setVisible(true);

        JLabel roomNameText = new JLabel("방 이름");
        roomNameText.setSize(new Dimension(100, 100));
        roomNameText.setLocation(new Point(1020, 125));
        roomNameText.setFont(new Font("SanSerif", Font.PLAIN, 24));
        roomNameText.setHorizontalAlignment(JLabel.CENTER);
        roomNameText.setVisible(true);
        page.add(roomNameText);

        roomName = new JTextField();
        roomName.setSize(new Dimension(200, 40));
        roomName.setLocation(new Point(1150, 155));
        roomName.setFont(new Font("SanSerif", Font.PLAIN, 24));
        roomName.setVisible(true);
        page.add(roomName);

        JLabel parsonText = new JLabel("최대 인원");
        parsonText.setSize(new Dimension(125, 100));
        parsonText.setLocation(new Point(1010, 250));
        parsonText.setFont(new Font("SanSerif", Font.PLAIN, 24));
        parsonText.setHorizontalAlignment(JLabel.CENTER);
        parsonText.setVisible(true);
        page.add(parsonText);

        String[] roomParsonData = new String[]{"2", "3", "4", "5", "6", "7", "8"};
        roomParson = new JComboBox<>(roomParsonData);
        roomParson.setSize(new Dimension(100, 40));
        roomParson.setLocation(new Point(1250, 280));
        roomParson.setFont(new Font("SanSerif", Font.PLAIN, 24));
        roomParson.setVisible(true);
        page.add(roomParson);

        JLabel roundText = new JLabel("라운드 수");
        roundText.setSize(new Dimension(125, 100));
        roundText.setLocation(new Point(1010, 375));
        roundText.setFont(new Font("SanSerif", Font.PLAIN, 24));
        roundText.setHorizontalAlignment(JLabel.CENTER);
        roundText.setVisible(true);
        page.add(roundText);

        String[] roundData = new String[]{"5", "10", "15", "20"};
        round = new JComboBox<>(roundData);
        round.setSize(new Dimension(100, 40));
        round.setLocation(new Point(1250, 405));
        round.setFont(new Font("SanSerif", Font.PLAIN, 24));
        round.setVisible(true);
        page.add(round);

        makeRoomButton = new JButton("방 만들기");
        makeRoomButton.setSize(new Dimension(300, 60));
        makeRoomButton.setLocation(new Point(1050, 500));
        makeRoomButton.setFont(new Font("SanSerif", Font.PLAIN, 28));
        makeRoomButton.addActionListener(e -> {
            makeRoom();
        });
        makeRoomButton.setVisible(true);
        page.add(makeRoomButton);

        page.add(makeRoomPanel);

        joinRoomButton = new JButton("방 입장");
        joinRoomButton.setSize(new Dimension(400, 80));
        joinRoomButton.setLocation(new Point(1000, 770));
        joinRoomButton.setFont(new Font("SanSerif", Font.PLAIN, 28));
        joinRoomButton.addActionListener(e -> {

        });
        joinRoomButton.setVisible(true);
        page.add(joinRoomButton);

        page.revalidate();
        page.repaint();
    }
}
