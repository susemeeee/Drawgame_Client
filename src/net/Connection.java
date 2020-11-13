/*
 * Connection.java
 * Author : 박찬형
 * Created Date : 2020-11-10
 */
package net;

import datatype.packet.Packet;

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

    private ByteArrayInputStream byteArrayInputStream;
    private ObjectInputStream objectInputStream;
    private ByteArrayOutputStream byteArrayOutputStream;
    private ObjectOutputStream objectOutputStream;

    public void connect(String address, int port){
        try {
            selector = Selector.open();
            client = SocketChannel.open(new InetSocketAddress(address, port));
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
            buffer = ByteBuffer.allocate(1048576);
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            byteArrayInputStream = null;
            objectInputStream = null;
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
        try {
            buffer.clear();
            ((SocketChannel)key.channel()).read(buffer);

            if(byteArrayInputStream == null){
                byteArrayInputStream = new ByteArrayInputStream(buffer.array());
                objectInputStream = new ObjectInputStream(byteArrayInputStream);
            }
            byteArrayInputStream.read(buffer.array());
            Packet data = (Packet) objectInputStream.readObject();
            System.out.println(data); // test
            //TODO 들어온 데이터 바인드
        } catch (IOException | ClassNotFoundException e) {
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
}
