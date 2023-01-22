package jpabook.start;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;

public class JpaMain {
    private static final EntityManagerFactory EMF = Persistence.createEntityManagerFactory("jpabook");

    public static void main(String[] args) {
        EntityManager em = EMF.createEntityManager();
        entityMerge();
//        EntityTransaction tx = em.getTransaction();
//
//        try {
//            tx.begin();
//            logic(em);
//            System.out.println("----------- before commit -----------");
//            tx.commit();
//
//        } catch (Exception e) {
//            tx.rollback();
//
//        } finally {
//            em.close();
//        }
//
//        EMF.close();
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

    private static void testMember(EntityManager entityManager) {
        String id = "testId3";
        Member member = new Member();
        member.setId(id);
        member.setUsername("testMember");
        member.setAge(10);
        entityManager.persist(member);
    }

    // flush mode 변경
    private static void flushModeChange(EntityManager entityManager) {
        entityManager.setFlushMode(FlushModeType.COMMIT);
    }

    // 특정 entity 준영속 상태로 변경
    private static void entityDetach(EntityManager entityManager) {
        Member findMember = entityManager.find(Member.class, "testId1");
        entityManager.detach(findMember);
    }

    // 모든 영속성 entity 준영속 상태로 변경
    private static void entityAllClear(EntityManager entityManager) {
        entityManager.clear();
    }

    private static void entityMerge() {
        Member member = createMember("mergeId", "회원1");
        member.setUsername("수정회원1");
        mergeMember(member);
    }

    private static Member createMember(String id, String name) {
        EntityManager em = EMF.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        Member member = new Member();
        try {
            tx.begin();

            member.setId(id);
            member.setUsername(name);
            member.setAge(10);

            em.persist(member);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        return member;
    }

    private static void mergeMember(Member member) {
        EntityManager em = EMF.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();
        Member mergeMember = em.merge(member);
        tx.commit();

        // 준영속 상태
        System.out.println("member = " + member);

        // 영속 상태
        System.out.println("mergeMember = " + mergeMember);

        System.out.println("em contains member : " + em.contains(member));
        System.out.println("em contains mergeMember : " + em.contains(mergeMember));

        em.close();
    }

}
