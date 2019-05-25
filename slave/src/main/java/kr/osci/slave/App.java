package kr.osci.slave;

import java.io.IOException;

public final class App {
    private App() {}

    public final static int port = 5050;

    public static void main(String[] args) {
        System.out.println("Slave started");
        System.out.println("Press enter to stop.");
        
        EMF emf = new EMF();
        emf.init();
        
        ListenerThread listener = new ListenerThread(port);
        try {
            listener.start();
            
            while (System.in.available() == 0 
                && listener.isAlive()) {
                Thread.sleep(1000);
            }
            listener.close();
            listener.join(1000);
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            emf.unInit();
        }
        System.out.println("Slave ended");
    }
}
