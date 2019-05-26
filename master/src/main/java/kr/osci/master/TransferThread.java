package kr.osci.master;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public class TransferThread extends Thread {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    
    private String host;
    private int port;
    private Socket socket;
    private BufferedReader br;
    private OutputStreamWriter osr;
    
    public TransferThread(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        
        try {
            while (isConnected()) {
                String line = "";
                try {
                    line = br.readLine();
                    
                    Date ackDate = sdf.parse(line);
                    // TODO: save
                } catch (ParseException | StringIndexOutOfBoundsException | NumberFormatException e) {
                    System.out.println("[ERROR] Wrong format : " + line);
                    this.close();
                    break;
                } catch (SocketTimeoutException e) {
                }
                
            }
        
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /*
     * return : last sent date
     */
    public Date transferNewerData(Date lastDate) {
        if (!this.isConnected()) {
            if (!this.connectToServer())
                return lastDate;
        }
        
        Date result = lastDate;
        List<TimeAndRandom> resultList = getNewerData(lastDate);
        resultList.forEach(System.out::println);

        for (TimeAndRandom x: resultList) {
            if (!this.sendData(x))
                break;
            result = x.getCreate_time(); 
        }
        
        return result;
    }

    private boolean sendData(TimeAndRandom x) {
        if (!this.isConnected()) {
            return false;
        }
        
        try {            
            String data = String.format("%s %d\n", x.getCreate_time(), x.getRandom());
            osr.write(sdf.format(data));
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
    
    private List<TimeAndRandom> getNewerData(Date lastDate) {
        EntityManager em = EMF.createEntityManager();
        
        TypedQuery<TimeAndRandom> query = em.createQuery(
                "SELECT t FROM TimeAndRandom t WHERE create_time > :lastACKDate ORDER BY create_time",
                TimeAndRandom.class).setMaxResults(30);
        query.setParameter("lastACKDate", lastDate);
        List<TimeAndRandom> resultList = query.getResultList();
        
        em.close();
        return resultList;
    }
    
    public void close() {
        try {
            if (socket != null)
                socket.close();
            socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }       
    }
}
