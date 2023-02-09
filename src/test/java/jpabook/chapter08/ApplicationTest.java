package jpabook.chapter08;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ApplicationTest {
    private EntityManagerFactory factory;
    private EntityManager manager;
    private EntityTransaction transaction;

    @BeforeEach
    void setup() {
        factory = Persistence.createEntityManagerFactory("jpabook");
        manager = factory.createEntityManager();
        transaction = manager.getTransaction();
        transaction.begin();
    }

    @AfterEach
    void clear() {
        manager.close();
        factory.close();
    }

    @Test
    void save() {

    }
}