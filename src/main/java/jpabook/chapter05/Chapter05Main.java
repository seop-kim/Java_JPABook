package jpabook.chapter05;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class Chapter05Main {
    public static void main(String[] args) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            Member member1 = new Member("member1", "회원1");
            Member member2 = new Member("member2", "회원2");

            em.persist(member1);
            em.persist(member2);

            Team team = new Team("team1", "팀1");
            em.persist(team);

            member1.setTeam(team);
            member2.setTeam(team);

            Team findTeam = member1.getTeam();
            System.out.println("member1 findTeam = " + findTeam);

            queryLogicJoin(em);
            updateRelation(em);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
    }

    private static EntityManager getEntityManager() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
        return emf.createEntityManager();
    }

    private static void queryLogicJoin(EntityManager entityManager) {
        String jpql = "select m from Member m join m.team t where " + "t.name=:teamName";
        List<Member> result = entityManager.createQuery(jpql, Member.class)
                .setParameter("teamName", "팀1")
                .getResultList();

        for (Member member : result) {
            System.out.println("member.username : " + member.getUsername());
        }
    }

    private static void updateRelation(EntityManager em) {
        Team team2 = new Team("team2", "팀2");
        em.persist(team2);

        Member member = em.find(Member.class, "member1");
        member.setTeam(team2);
    }
}
