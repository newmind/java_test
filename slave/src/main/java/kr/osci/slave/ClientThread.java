package kr.osci.slave;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;

import kr.osci.slave.TimeAndRandom;

public class ClientThread extends Thread {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");    

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

            // 1. thread blocking 방지, 
            // 2. 받은 데이터에 대한 ACK를 reply 
            this.socket.setSoTimeout(200);
            
            InputStream in = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);
            
            OutputStream out = socket.getOutputStream();
            OutputStreamWriter osr = new OutputStreamWriter(out);

            String lastRecvDate = "";
            while (socket != null) {
                String line = "";
                try {
                    line = br.readLine();
                    System.out.println(line);
                    // format : "2019-05-25 01:30:25.982 19457190"
                    lastRecvDate = line.substring(0, 24);
                    int random = Integer.parseInt(line.substring(25));
                    
                    createTimeAndRandom(sdf.parse(lastRecvDate), random);
                    
                } catch (SocketTimeoutException e) {
                    if (!lastRecvDate.isEmpty()) {
                        // ACK
                        osr.write(lastRecvDate + "\n");
                        osr.flush();
                        lastRecvDate = "";
                    }
                } catch (ParseException | StringIndexOutOfBoundsException | NumberFormatException e) {
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
    
    private void createTimeAndRandom(Date date, int random) {
        try {
            //TODO: insert 부하 발생시, 배치로 처리 필요
            em.getTransaction().begin();
            TimeAndRandom timeAndRandom = new TimeAndRandom(date, random);
            em.persist(timeAndRandom);
            em.getTransaction().commit();
        } catch (EntityExistsException e) {
            //TODO: 중복된다면 처리 필요
            System.out.println("[ERROR] 중복 데이터 저장 시도");
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
