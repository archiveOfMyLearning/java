package com.edu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends Thread {
    private static final int PORT = 999;
    private static final String HOST = "127.0.0.1";

    private final PrintWriter writer;
    private final BufferedReader reader;
    public Client(Socket socket) throws IOException {
        writer = new PrintWriter(socket.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    @Override
    public void run(){
        Thread consoleReader = new Thread(()->{
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String msg = null;
            while (true) {
                try {
                    msg = stdIn.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                writer.println(msg);
                if (msg.equals("quit")) break;
            }
        });
        consoleReader.start();

        Thread serverListener = new Thread(()->{
            String msg;
            try {
                while ((msg=reader.readLine())!=null) {System.out.println(msg);}
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverListener.setDaemon(true);
        serverListener.start();
    }

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(HOST,PORT);
            Client client = new Client(socket);
            client.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
