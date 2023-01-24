package jpabook.chapter05;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Chapter05MainTest {
    private EntityManagerFactory factory;
    private EntityManager manager;
    private EntityTransaction transaction;

    @BeforeEach
    void setup() {
        factory = Persistence.createEntityManagerFactory("jpabook");
        manager = factory.createEntityManager();
        transaction = manager.getTransaction();
    }

    @AfterEach
    void clear() {
        manager.close();
        factory.close();
    }

    @Test
    public void testORM_양방향() {
        transaction.begin();
        Team team1 = new Team("team1", "팀");
        manager.persist(team1);

        Member member1 = new Member("member1", "회원1");

        member1.setTeam(team1);
        team1.getMembers().add(member1);
        manager.persist(member1);

        Member member2 = new Member("member2", "회원2");
        member2.setTeam(team1);
        team1.getMembers().add(member2);
        manager.persist(member2);

        transaction.commit();
    }

    @Test
    public void testORM_양방향_리팩토링() {
        transaction.begin();
        Team team1 = new Team("team1", "팀");
        manager.persist(team1);

        Member member1 = new Member("member1", "회원1");

        member1.setTeam(team1);
        manager.persist(member1);

        Member member2 = new Member("member2", "회원2");
        member2.setTeam(team1);
        manager.persist(member2);

        transaction.commit();
    }
}