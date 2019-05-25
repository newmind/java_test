package kr.osci.slave;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import javax.persistence.EntityManager;

public class ClientThread extends Thread {
    private Socket socket;
    private EntityManager em;
    
    public ClientThread(Socket socket) {
        super();
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            this.em = EMF.createEntityManager();

            this.socket.setSoTimeout(1000);
            InputStream in = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);

            while (socket != null) {
                String line = "";
                try {
                    line = br.readLine();
                    System.out.println(line);
                    // format : "2019-05-25 01:30:25.982 19457190"
                    String sDate = line.substring(0, 24);
                    int random = Integer.parseInt(line.substring(25));
                } catch (SocketTimeoutException e) {
                    continue;
                } catch (StringIndexOutOfBoundsException | NumberFormatException e) {
                    System.out.println("[ERROR] Wrong format : " + line);
                    this.socket.close();
                    this.socket = null;
                    break;
                } catch (SocketException e) {
                    break;
                }
            }                   
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void close() {
        try {
            if (socket != null)
                socket.close();
            em.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        socket = null;
    }
}
