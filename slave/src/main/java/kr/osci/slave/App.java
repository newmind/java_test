package kr.osci.slave;

import java.io.IOException;

public final class App {
    private App() {}

    public final static int port = 5050;

    public static void main(String[] args) {
        System.out.println("Slave started");
        
        Listener listener = new Listener(port);
        try {
            listener.start();
            
            while (System.in.available() == 0 
                && listener.isAlive()) {
                Thread.sleep(1000);
            }
            listener.close();
            listener.join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Slave ended");
    }
}
