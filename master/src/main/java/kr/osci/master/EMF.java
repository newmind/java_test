package kr.osci.master;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EMF {
    final private static String pu = "time-and-random";

    private static EntityManagerFactory emf;

    public void init() {
        emf = Persistence.createEntityManagerFactory(pu);
    }

    public void unInit() {
        emf.close();
        emf = null;
    }

    public static EntityManager createEntityManager() {
        if (emf == null) {
            throw new IllegalStateException("EMF is not initialized yet.");
        }
        return emf.createEntityManager();
    }
}

