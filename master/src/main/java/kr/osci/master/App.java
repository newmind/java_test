package kr.osci.master;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class App {
    private App() {}
    
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");    
    private static TransferThread transferThread;
    
    public static void main(String[] args) {
        System.out.println("Master started");
        System.out.println("Press enter to stop.");
        
        EMF emf = new EMF();
        emf.init();
        
        transferThread = new TransferThread("localhost", 5050); 
        try {
            transferThread.start();
            
            Date lastSentDate = transferThread.loadACKDate();
            
            while (System.in.available() == 0) {
                long startTime = System.currentTimeMillis();

                lastSentDate = transferThread.transferNewerData(lastSentDate);
                
                long finishTime = System.currentTimeMillis();
                Thread.sleep(Math.max(0, 1000 - (finishTime - startTime)));
            }
            transferThread.close();
            transferThread.join(1000);
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        emf.unInit();
        System.out.println("Master ended");
    }
}
