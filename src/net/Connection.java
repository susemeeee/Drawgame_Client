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

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
        //TODO 로그인 패킷 보내기
    }

    private void read(SelectionKey key){
        buffer = ByteBuffer.allocate(1048576);
        ByteBuffer data = ByteBuffer.allocate(1048576);
        try {
            buffer.clear();
            data.clear();
            SocketChannel socketChannel = ((SocketChannel)key.channel());

            while(client.read(data) > 0){
                data.flip();
                buffer.put(data);
                data = ByteBuffer.allocate(1048576);
            }
            buffer.flip();
            byte[] array = new byte[buffer.limit()];
            buffer.get(array, 0, buffer.limit());
            Map<String, String> receivedPacket = DataMaker.make(new String(array));

            if(PacketType.valueOf(receivedPacket.get("type")) == PacketType.RESPONSE_ROOM){
                responseRoomData(receivedPacket);
            }

            System.out.println(receivedPacket.toString()); // test
            //TODO 들어온 데이터 바인드
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Packet data){
        try {
            Charset charset = StandardCharsets.UTF_8;
            buffer = charset.encode(data.toString());
            client.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void responseRoomData(Map<String, String> receivedPacket){
        int totalRoom = Integer.parseInt(receivedPacket.get("totalroom"));
        if(totalRoom != 0){
            String[] roomNames = new String[totalRoom];
            String[] roomMaxPerson = new String[totalRoom];
            String[] roomCurrentPerson = new String[totalRoom];

            for(int i = 0; i < totalRoom; i++){
                roomNames[i] = receivedPacket.get("room" + (i + 1) + "_name");
                roomMaxPerson[i] = receivedPacket.get("room" + (i + 1) + "_maxuser");
                roomCurrentPerson[i] = receivedPacket.get("room" + (i + 1) + "_currentuser");
            }

            ClientFrame.getInstance().responseRoomData(totalRoom, roomNames, roomMaxPerson, roomCurrentPerson);
        }
        else{
            ClientFrame.getInstance().responseRoomData(0, null,
                    null, null);
        }
    }
}
