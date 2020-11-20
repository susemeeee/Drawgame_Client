/*
 * Connection.java
 * Author : 박찬형
 * Created Date : 2020-11-10
 */
package net;

import UI.ClientFrame;
import datatype.packet.Packet;
import datatype.packet.PacketType;
import util.DataMaker;

import javax.swing.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;

public class Connection {
    private SocketChannel client;
    private Selector selector;
    private Thread clientThread;
    private ByteBuffer buffer;

    public void connect(String address, int port){
        try {
            selector = Selector.open();
            client = SocketChannel.open(new InetSocketAddress(address, port));
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }

        clientThread = new Thread(() -> {
            while(true){
                try {
                    selector.select();
                    Iterator iterator = selector.selectedKeys().iterator();

                    while(iterator.hasNext()){
                        SelectionKey key = (SelectionKey)iterator.next();

                        if(key.isReadable()){
                            read(key);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        clientThread.start();
    }

    private void read(SelectionKey key){
        buffer = ByteBuffer.allocate(1048576);
        ByteBuffer data = ByteBuffer.allocate(1024);
        try {
            buffer.clear();
            data.clear();

            while(client.read(data) > 0){
                data.flip();
                buffer.put(data);
                data = ByteBuffer.allocate(1024);
            }
            buffer.flip();
            byte[] array = new byte[buffer.limit()];
            buffer.get(array, 0, buffer.limit());
            Map<String, String> receivedPacket = DataMaker.make(new String(array));

            PacketType type = PacketType.valueOf(receivedPacket.get("type"));
            if(type == PacketType.RESPONSE_ROOM){
                responseRoomData(receivedPacket);
            }
            else if(type == PacketType.RESPONSE_USER){
                responseUserData(receivedPacket);
            }
            else if(type == PacketType.JOIN_ROOM_RESULT){
                responseJoinRoomResult(receivedPacket);
            }
            else if(type == PacketType.CHAT){
                ClientFrame.getInstance().chatReceived(receivedPacket.get("sender"), receivedPacket.get("content"));
            }
            else if(type == PacketType.READY){
                readyStatusReceived(receivedPacket);
            }
            else if(type == PacketType.START_REQUEST){
                ClientFrame.getInstance().responseStartResult(Boolean.parseBoolean(receivedPacket.get("status")));
            }
            else if(type == PacketType.DISCONNECT){
                client.close();
                JOptionPane.showMessageDialog(null, "서버가 종료 되었습니다.", "error",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(2);
            }

            //System.out.println(receivedPacket.toString()); // test
        } catch (IOException e) {
            disconnect();
            System.exit(1);
        }
    }

    public void send(Packet data){
        try {
            Charset charset = StandardCharsets.UTF_8;
            buffer = charset.encode(data.toString());
            client.write(buffer);
        } catch (IOException e) {
            try {
                client.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.exit(1);
        }
    }

    public void disconnect(){
        Packet packet = new Packet(PacketType.DISCONNECT);
        send(packet);
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public boolean isConnected(){
        if(client == null){
            return false;
        }
        return client.isConnected();
    }

    private void responseRoomData(Map<String, String> receivedPacket){
        int totalRoom = Integer.parseInt(receivedPacket.get("totalroom"));
        if(totalRoom != 0){
            String[] roomNames = new String[totalRoom];
            String[] roomMaxPerson = new String[totalRoom];
            String[] roomCurrentPerson = new String[totalRoom];
            int[] roomIDList = new int[totalRoom];

            for(int i = 0; i < totalRoom; i++){
                roomNames[i] = receivedPacket.get("room" + (i + 1) + "_name");
                roomMaxPerson[i] = receivedPacket.get("room" + (i + 1) + "_maxuser");
                roomCurrentPerson[i] = receivedPacket.get("room" + (i + 1) + "_currentuser");
                roomIDList[i] = Integer.parseInt(receivedPacket.get("room" + (i + 1) + "_id"));
            }

            ClientFrame.getInstance().responseRoomData(totalRoom, roomNames, roomMaxPerson, roomCurrentPerson,
                    roomIDList);
        }
        else{
            ClientFrame.getInstance().responseRoomData(0, null,
                    null, null, null);
        }
    }

    private void responseUserData(Map<String, String> receivedPacket){
        int totalUser = Integer.parseInt(receivedPacket.get("totaluser"));
        int currentUser = Integer.parseInt(receivedPacket.get("currentuser"));
        int[] IDList = new int[totalUser];
        String[] names = new String[totalUser];
        ImageIcon[] icons = new ImageIcon[totalUser];
        int yourID = Integer.parseInt(receivedPacket.get("yourID"));

        for(int i = 0; i < totalUser; i++){
            if(receivedPacket.get("user" + i + "_id") != null){
                IDList[i] = Integer.parseInt(receivedPacket.get("user" + i + "_id"));
                names[i] = receivedPacket.get("user" + i + "_name");
                byte[] imageBytes = Base64.getDecoder().decode(receivedPacket.get("user" + i + "_characterIcon"));
                icons[i] = new ImageIcon(imageBytes);
            }
        }

        ClientFrame.getInstance().responseUserData(currentUser, totalUser, IDList, names, icons, yourID);
    }

    private void responseJoinRoomResult(Map<String, String> receivedPacket){
        String result = receivedPacket.get("result");
        ClientFrame.getInstance().responseJoinRoomResult(result);
    }

    private void readyStatusReceived(Map<String, String> receivedPacket){
        boolean[] readyStatus = new boolean[8];
        int[] idList = new int[8];

        for(int i = 0; i < 8; i++){
            readyStatus[i] = Boolean.parseBoolean(receivedPacket.get("user" + i + "_readystatus"));
            idList[i] = i;
        }

        ClientFrame.getInstance().readyStatusReceived(readyStatus, idList);
    }
}
