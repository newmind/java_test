package kr.osci.slave;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ListenerThread extends Thread {
    private ServerSocket sockServer;
    private int port;

    private ClientThread threadClient;
    
    public ListenerThread(int port) {
        this.port = port;
    }
    
    @Override
    public void run() {
        try {
            sockServer = new ServerSocket(port);
            System.out.println(String.format("Listening on port %d", this.port));

            while (true) {
                Socket socket = sockServer.accept();
                if (this.threadClient != null) { 
                    this.threadClient.close();
                    this.threadClient.join(10);
                }
                
                this.threadClient = new ClientThread(socket);
                this.threadClient.start();
            }
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (sockServer != null)
                sockServer.close();
            sockServer = null;
            if (threadClient != null) {
                threadClient.close();
                threadClient.join(1000);
            }
            threadClient = null;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}   
