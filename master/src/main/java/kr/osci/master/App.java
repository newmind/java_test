package kr.osci.master;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

public class App {
    private App() {}
    
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("time-and-random");
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");    

    public static void main(String[] args) {
        System.out.println("Master started");
        System.out.println("Press any key to stop.");
        
        
        try {
            Date lastSentDate = getACKDate();
            
            while (System.in.available() == 0) {
                transferNewerData(lastSentDate);
                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        
        return;
    }

    private static void transferNewerData(Date lastDate) {
        List<TimeAndRandom> resultList = getNewerData(lastDate);
        
        resultList.forEach(System.out::println);
        for (int i = 0; i < resultList.size(); i++) {
            
        }
    }

    private static List<TimeAndRandom> getNewerData(Date lastDate) {
        EntityManager em = emf.createEntityManager();
        
        TypedQuery<TimeAndRandom> query = em.createQuery(
                "SELECT t FROM TimeAndRandom t WHERE create_time > :lastsentdate",
//                "SELECT t FROM TimeAndRandom t ORDER BY create_time DESC",
                TimeAndRandom.class).setMaxResults(30);
        query.setParameter("lastsentdate", lastDate, TemporalType.DATE);
        List<TimeAndRandom> resultList = query.getResultList();
        
        em.close();
        return resultList;
    }

    /*
     * 마지막으로 전송에 성공한 데이터의 시간
     */
    private static Date getACKDate() {
        try {
            return sdf.parse("2019-05-25 11:59:26");
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new Date();
    }
}
