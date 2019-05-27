package kr.osci.master;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;


public class TransferThread extends Thread {
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private final String savedACKDateFile = "lastACK.dat"; 
    
    private EntityManager em;
    
    private String host;
    private int port;
    private Socket socket;
    private BufferedReader br;
    private OutputStreamWriter osr;
    private boolean closed = false;
    
    public TransferThread(String host, int port) {
        this.host = host;
        this.port = port;
        
//        this.em = EMF.createEntityManager();
    }

    @Override
    public void run() {
        
        try {
            while (!closed) {
                while (isConnected()) {
                    String line = "";
                    try {
                        line = br.readLine();
                        
                        Date ackDate = sdf.parse(line);
                        System.out.println("received " + line);
                        this.saveACKDate(ackDate);
                    } catch (ParseException | StringIndexOutOfBoundsException | NumberFormatException e) {
                        System.out.println("[ERROR] Wrong format : " + line);
                        this.close();
                        break;
                    } catch (SocketTimeoutException e) {
                    }
                }
                Thread.sleep(0);
            }
        
        } catch (Exception e) {
        }
    }

    /*
     * return : last sent date
     */
    public Date transferNewerData(Date lastDate) {
        if (!this.isConnected()) {
            if (!this.connectToServer())
                return lastDate;
            // 마지막 전송성공한 것부터 보낼수 있게 처리.
            lastDate = loadACKDate();
        }
        
        Date result = lastDate;
        List<TimeAndRandom> resultList = getNewerData(lastDate);
        
        for (TimeAndRandom x: resultList) {
            if (!this.sendData(x))
                break;
            result = x.getCreate_time(); 
        }
        
        return result;
    }

    private List<TimeAndRandom> getNewerData(Date lastDate) {
        final int MAX_FETCH_SIZE = 1000;
        
        EntityManager em = EMF.createEntityManager();
        em.getTransaction().begin();
        
        TypedQuery<TimeAndRandom> query = em.createQuery(
                "SELECT t FROM TimeAndRandom t WHERE create_time > :lastACKDate ORDER BY create_time",
                TimeAndRandom.class).setMaxResults(MAX_FETCH_SIZE);
        query.setParameter("lastACKDate", lastDate);
        List<TimeAndRandom> resultList = query.getResultList();
        
        em.getTransaction().commit();
        em.close();
        return resultList;
    }

    private boolean sendData(TimeAndRandom x) {
        if (!this.isConnected()) {
            return false;
        }
        
        try {            
            String data = String.format("%s %d\n", sdf.format(x.getCreate_time()), x.getRandom());
            osr.write(data);
            osr.flush();
        } catch (IOException e) {
            e.printStackTrace();
            this.close();
            return false;
        }
        
        return true;
    }

    private boolean connectToServer() {
        try {
            Socket socket = new Socket(this.host, this.port);
            socket.setSoTimeout(200);
            
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            this.br = new BufferedReader(new InputStreamReader(in));
            this.osr = new OutputStreamWriter(out);
            this.socket = socket;
            return true;
        } catch (IllegalArgumentException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
        
    public void close() {
        try {
            if (socket != null)
                socket.close();
            socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        closed = true;
    }

    private void saveACKDate(Date ackDate) {
        try {
            FileOutputStream fos = new FileOutputStream(savedACKDateFile, false);
            DataOutputStream dos = new DataOutputStream(fos);
            
            dos.writeUTF(sdf.format(ackDate));
            
            dos.flush();
            dos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /*
     * 마지막으로 전송에 성공한 데이터의 시간
     * 처음 시작하는 거라면, 현재 시간 리턴
     */
    public Date loadACKDate() {
        Date date = new Date();
        try {
            FileInputStream fis = new FileInputStream(savedACKDateFile);
            DataInputStream dis = new DataInputStream(fis);
            
            String sDate = dis.readUTF();
            date = sdf.parse(sDate);
            
            dis.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;

    }
}
