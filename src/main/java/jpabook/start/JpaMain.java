package jpabook.start;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            logic(em);
            System.out.println("----------- before commit -----------");
            tx.commit();

        } catch (Exception e) {
            tx.rollback();

        } finally {
            em.close();
        }

        emf.close();
    }

    private static void logic(EntityManager em) {
        String id = "id1";

        // 객체 생성
        Member member = new Member();

        // 객체 값 삽입
        member.setId(id);
        member.setUsername("지한");
        member.setAge(2);

        // 등록
        em.persist(member);

        // 수정
        member.setAge(20);

        // 조횐
        Member findMember = em.find(Member.class, id);
        System.out.println(findMember.toString());

        sameMember(em);

        // 전체 조회
        List<Member> members =
                em.createQuery("select m from Member m", Member.class)
                        .getResultList();

        System.out.println("Members.size : " + members.size());

        // 삭제
        em.remove(member);
    }

    private static void sameMember(EntityManager em) {
        Member findMember1 = em.find(Member.class, "id1");
        Member findMember2 = em.find(Member.class, "id1");
        System.out.println("member1 == member2 : " + (findMember1 == findMember2));
    }
}
