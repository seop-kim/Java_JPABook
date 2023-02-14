package jpabook.chapter09;

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
//        createMemberAndTeamTest();
    }

    private void createMemberAndTeamTest() {
        Member member = new Member();
        member.setId("member1");
        member.setUsername("member1");
        Team team = new Team("team1", "team1_name");
        manager.persist(team);
        member.setTeam(team);
        manager.persist(member);
        transaction.commit();
        manager.clear();
    }

    @AfterEach
    void clear() {
        manager.close();
        factory.close();
    }

    @Test
    void save() {

    }

    @Test
    public void printUserAndTeam(String memberId) {
//        Member findMember = manager.find(Member.class, memberId);
//        Team findTeam = findMember.getTeam();
//        System.out.println("findMember = " + findMember);
//        System.out.println("findTeam = " + findTeam);
    }

    @Test
    public void printUser(String memberId) {
//        Member findMember = manager.find(Member.class, memberId);
//        System.out.println("findMember = " + findMember);
//        Member member = manager.getReference(Member.class, memberId);
//        member.getUsername();

    }

    @Test
    void print() {
        Member member = manager.find(Member.class, "member1");
        System.out.println("member = " + member);
    }

    @Test
    void saveNoCaseCade() {
        // Parent save
        Parent parent = new Parent();
        manager.persist(parent);

        // No.1 child save
        Child child1 = new Child();
        child1.setParent(parent); // child.parent setting
        parent.getChildren().add(child1); // parent.child setting
        manager.persist(child1);

        // No.2 child save
        Child child2 = new Child();
        child2.setParent(parent); // child.parent setting
        parent.getChildren().add(child2); // parent.child setting
        manager.persist(child2);
        transaction.commit();
    }


    void saveWithCascade() {
        Child child1 = new Child();
        Child child2 = new Child();

        Parent parent = new Parent();
        child1.setParent(parent);
        child2.setParent(parent);
        parent.getChildren().add(child1);
        parent.getChildren().add(child2);

        manager.persist(parent);
        manager.persist(child1);
        manager.persist(child2);
        transaction.commit();
    }

    @Test
    void test() {
        saveWithCascade();
        manager.clear();

        Parent parent1 = manager.find(Parent.class, 1L);
        System.out.println("after parent1 : " + parent1);
        parent1.getChildren().remove(0);
        System.out.println("before parent1 : " + parent1);
        manager.persist(parent1);

        manager.clear();

        parent1 = manager.find(Parent.class, 1L);
        System.out.println("finish parent1 : " + parent1);
    }

    @Test
    void 교재코드() {
        Parent parent = manager.find(Parent.class, 1L);
        parent.getChildren().remove(0);
    }

    @Test
    void test2() {
        Parent parent = new Parent();
        manager.persist(parent);

        Child child1 = new Child();
        child1.setParent(parent);
        parent.getChildren().add(child1);

        Child child2 = new Child();
        child2.setParent(parent);
        parent.getChildren().add(child2);

        manager.flush();
        manager.clear();

        Parent fParent = manager.find(Parent.class, 1L);

        fParent.getChildren().remove(0);
        System.out.println("fParent = " + fParent);
        manager.flush();
        manager.clear();

        Parent fParent2 = manager.find(Parent.class, 1L);
        System.out.println("fParent2 = " + fParent2);
    }
}