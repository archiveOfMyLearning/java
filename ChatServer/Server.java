package com.edu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {
    private record Message (String clientId, String text){}

    private static BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();
    private static Map<String,ClientConnection> clientThreads = new ConcurrentHashMap<>();
    private static class ClientConnection extends Thread{
        Socket socket;
        PrintWriter writer;
        BufferedReader reader;
        String clientId;
        public ClientConnection(Socket socket) {
            try{
                this.socket = socket;
                this.writer = new PrintWriter(socket.getOutputStream(),true);
                this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                clientId= "User " + UUID.randomUUID();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        @Override
        public void run(){
            try {
                clientThreads.put(clientId, this);
                messageQueue.put(new Message("System: ",clientId + " connected"));
                while (true) {
                    if (reader.ready()) {
                        String msgText = reader.readLine();
                        messageQueue.put(new Message(clientId, msgText));
                        if ("quit".equals(msgText)) break;
                    }
                    Thread.sleep(100);
                }
                clientThreads.remove(clientId);
                socket.close();
                messageQueue.put(new Message("System: ",clientId + " disconnected"));
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private static final int PORT = 999;
    public static void main(String[] args) {
        System.out.println("===MVP char server===");
        Thread lobby = new Thread(()->{
            try(ServerSocket serverSocket = new ServerSocket(PORT)){
                while (true){
                    Socket socket = serverSocket.accept();
                    new ClientConnection(socket).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        lobby.start();

        Thread broadcaster = new Thread(()->{
            while (true) {
                try {
                    Message message = messageQueue.take();
                    for (ClientConnection connection : clientThreads.values()) {
                        connection.writer.println(message.clientId+": "+message.text);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });
        broadcaster.start();


    }


}
