package kr.osci.master;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public class App {
    private App() {}
    
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");    

    public static void main(String[] args) {
        System.out.println("Master started");
        System.out.println("Press enter to stop.");
        
        EMF emf = new EMF();
        emf.init();
        
        try {
            Date lastACKDate = loadACKDate();
            
            while (System.in.available() == 0) {
                long startTime = System.currentTimeMillis();

                lastACKDate = transferNewerData(lastACKDate);
                
                long finishTime = System.currentTimeMillis();
                Thread.sleep(Math.max(0, 1000 - (finishTime - startTime)));
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        emf.unInit();
        System.out.println("Master ended");
    }

    private static Date transferNewerData(Date lastDate) {
        Date result = lastDate;

        List<TimeAndRandom> resultList = getNewerData(lastDate);
        
        resultList.forEach(System.out::println);
        for (TimeAndRandom x: resultList) {
            result = x.getCreate_time(); 
        }
        
        return result;
    }

    private static List<TimeAndRandom> getNewerData(Date lastDate) {
        EntityManager em = EMF.createEntityManager();
        System.out.println("getNewerData - " + em.toString());
        
        TypedQuery<TimeAndRandom> query = em.createQuery(
                "SELECT t FROM TimeAndRandom t WHERE create_time > :lastACKDate",
//                "SELECT t FROM TimeAndRandom t ORDER BY create_time DESC",
                TimeAndRandom.class).setMaxResults(30);
        query.setParameter("lastACKDate", lastDate);
        List<TimeAndRandom> resultList = query.getResultList();
        
        em.close();
        return resultList;
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
