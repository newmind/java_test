package kr.osci.slave;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener extends Thread {
    private ServerSocket server;
    private int port;
    
    public Listener(int port) {
        this.port = port;
    }
    
    @Override
    public void run() {
        try {
            Thread.sleep(5000);
//            server = new ServerSocket(port);
//            
//            while (true) {
//                Socket socket = server.accept();
//            }
//            
        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void close() {
        try {
            if (server != null)
                server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        server = null;
    }
}   
