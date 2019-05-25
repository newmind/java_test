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
            
            final int MAX_ACK_HOLDS = 100; // 몇개마다 ACK 보낼지
            int ackHolds = 0; 
            
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
                    
                    ackHolds++;
                    if (ackHolds >= MAX_ACK_HOLDS) {
                        sendACK(lastRecvDate);
                        lastRecvDate = "";
                        ackHolds = 0;
                    }
                } catch (SocketTimeoutException e) {
                    if (ackHolds > 0 && !lastRecvDate.isEmpty()) {
                        // ACK
                        sendACK(lastRecvDate);
                        lastRecvDate = "";
                        ackHolds = 0;
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

    private void sendACK(String lastRecvDate) {
        try {
            OutputStream out = socket.getOutputStream();
            OutputStreamWriter osr = new OutputStreamWriter(out);
            
            osr.write(lastRecvDate + "\n");
            osr.flush();
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
            System.out.println("[ERROR] 중복 데이터 저장 시도 : " + sdf.format(date));
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
