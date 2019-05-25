package kr.osci.random;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class App {
    private App() {}

    private static EntityManager em;
    private static Random random = new Random();

    public static void main(String[] args) {
        System.out.println("Generator started");
        System.out.println("Press enter to stop.");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("time-and-random");
        em = emf.createEntityManager();

        try {
            while (System.in.available() == 0) {
                createTimeAndRandom();
                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        em.close();
        emf.close();
        System.out.println("Generator ended");
        return;
    }

    private static void createTimeAndRandom() {
        em.getTransaction().begin();
        TimeAndRandom timeAndRandom = new TimeAndRandom(new Date(), random.nextInt());
        em.persist(timeAndRandom);
        em.getTransaction().commit();
    }
}
