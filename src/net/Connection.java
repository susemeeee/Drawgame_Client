/*
 * Connection.java
 * Author : 박찬형
 * Created Date : 2020-11-10
 */
package net;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Connection {
    private SocketChannel client;
    private Selector selector;
    private Thread clientThread;

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
        send("hello");
        //TODO 로그인 패킷 보내기
    }

    private void read(SelectionKey key){
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        try {
            buffer.clear();
            ((SocketChannel)key.channel()).read(buffer);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer.array());
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            String data = (String)objectInputStream.readObject();
            System.out.println(data); // test
            //TODO 들어온 데이터 바인드
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void send(String data){
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(data);
            objectOutputStream.flush();
            client.write(ByteBuffer.wrap(byteArrayOutputStream.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
