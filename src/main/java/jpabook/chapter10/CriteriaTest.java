package jpabook.chapter10;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class CriteriaTest {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
    private static final EntityManager em = emf.createEntityManager();

    // Criteria query builder
    private static final CriteriaBuilder cb = em.getCriteriaBuilder();

    public static void main(String[] args) {
        CriteriaTest ct = new CriteriaTest();
        ct.testMethod();
    }

    public void testMethod() {

        // Criteria 생성, 반환 타입 지정
        CriteriaQuery<Member> cq = cb.createQuery(Member.class);

        // from
        Root<Member> m = cq.from(Member.class);

        // 검색 조건 추가
        Predicate usernameEqual = cb.equal(m.get("name"), "회원1"); // getName == "회원1"
        Predicate userAgeGt = cb.gt(m.<Integer>get("age"), 10); // getAge > 10

        //정렬 조건 추가
        Order ageDesc = cb.desc(m.get("age"));

        // select
        cq.select(m)
                .where(userAgeGt, usernameEqual)
                .orderBy(ageDesc);

        List<Member> members = em.createQuery(cq).getResultList();

        System.out.println("members = " + members);

    }


    public void distinctMethod() {
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<Member> m = cq.from(Member.class);
        cq.multiselect(m.get("name"), m.get("age")).distinct(true);

        TypedQuery<Object[]> query = em.createQuery(cq);
        List<Object[]> resultList = query.getResultList();

        System.out.println("resultList = " + resultList);
    }
}
