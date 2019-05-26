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
            
            Date lastSentDate = loadACKDate();
            
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

    /*
     * 마지막으로 전송에 성공한 데이터의 시간
     */
    private static Date loadACKDate() {
        try {
            //TODO 저장된 마지막날짜 가져오기
            return sdf.parse("2019-05-25 11:59:26.000");
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new Date();
    }
}
