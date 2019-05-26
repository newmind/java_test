package kr.osci.master;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
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
   
    public TransferThread(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        
        super.run();
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
            OutputStream out = socket.getOutputStream();
            OutputStreamWriter osr = new OutputStreamWriter(out);
            
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
                "SELECT t FROM TimeAndRandom t WHERE create_time > :lastACKDate",
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
