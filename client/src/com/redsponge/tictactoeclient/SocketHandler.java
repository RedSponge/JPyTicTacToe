package com.redsponge.tictactoeclient;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.IO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class SocketHandler {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private int port;
    private String ip;
    private InetAddress address;
    private Thread listener;

    public SocketHandler(String ip, int port) throws IOException {
        this.port = port;
        this.ip = ip;
        this.address = InetAddress.getByName(ip);

        createSocket();

        listener = new Thread(this::loopReceive);
        listener.start();
    }

    private void createSocket() throws IOException {
        socket = new Socket(address, port);
        out = new PrintWriter(socket.getOutputStream());
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public String receive() {
        try {
            return in.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loopReceive() {
        while(true) {
            try {
                String data = in.readLine();
                onDataReceived(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onDataReceived(String data) {
        System.out.println("RECEIVED DATA " + data);
    }

    public void send(String s) {
        out.println(s);
        out.flush();
    }

    public void close() {
        try {
            listener.join();
            out.close();
            in.close();
            socket.close();
        } catch (IOException|InterruptedException e) {
            e.printStackTrace();
        }
    }

}
