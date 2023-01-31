package jpabook.chapter06;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Chapter06MainTest {
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
    void testSave() {
        Member member1 = new Member("member1", "멤버1");
        Member member2 = new Member("member2", "멤버2");
        Team team = new Team("team1", "팀1");
        manager.persist(member1);
        manager.persist(member2);

        manager.persist(team);
        team.getMembers().add(member1);
        team.getMembers().add(member2);

        System.out.println("member1 team : " + member1.getTeam());
        transaction.commit();
    }

    @Test
    void ManyToManySaveTest() {
//        Product productA = new Product();
//        productA.setId("productA");
//        productA.setName("상품A");
//        manager.persist(productA);
//
//        Member member1 = new Member();
//        member1.setId("member1");
//        member1.setUsername("회원1");
//        member1.getProducts().add(productA);
//        manager.persist(member1);
//
//        transaction.commit();
    }

    @Test
    void ManyToManyFindTest() {
//        Member member = manager.find(Member.class, "member1");
//        List<Product> products = member.getProducts();
//        for (Product product : products) {
//            System.out.println("product = " + product.getName());
//        }
    }

    @Test
    void ManyToManyFindInverse() {
//        Product product = manager.find(Product.class, "productA");
//        List<Member> members = product.getMembers();
//        for (Member member : members) {
//            System.out.println("member = " + member);
//        }
    }

    @Test
    void ManyToManyIdentifySaveTest() {
//        Member member1 = new Member();
//        member1.setId("member1");
//        member1.setUsername("회원1");
//        manager.persist(member1);
//
//        Product productA = new Product();
//        productA.setId("productA");
//        productA.setName("상품1");
//        manager.persist(productA);
//
//        MemberProduct memberProduct = new MemberProduct();
//        memberProduct.setMember(member1);
//        memberProduct.setProduct(productA);
//        memberProduct.setOrderAmount(2);
//        manager.persist(memberProduct);
//
//        transaction.commit();
    }

    @Test
    void ManyToManyIdentifyFindTest() {
//        ManyToManyIdentifySaveTest();
//        MemberProductId memberProductId = new MemberProductId();
//        memberProductId.setMember("member1");
//        memberProductId.setProduct("productA");
//
//        MemberProduct memberProduct = manager.find(MemberProduct.class, memberProductId);
//
//        Member member = memberProduct.getMember();
//        Product product = memberProduct.getProduct();
//
//        System.out.println("member = " + member);
//        System.out.println("product = " + product);
//        System.out.println("count = " + memberProduct.getOrderAmount());
    }

    @Test
    void ManyToManyPrimarySaveTest() {
        Member member1 = new Member();
        member1.setId("member1");
        member1.setUsername("회원1");
        manager.persist(member1);

        Product productA = new Product();
        productA.setId("productA");
        productA.setName("상품1");
        manager.persist(productA);

        Order order = new Order();
        order.setMember(member1);
        order.setProduct(productA);
        order.setOrderAmount(2);
        manager.persist(order);

        transaction.commit();
    }

    @Test
    void ManyToManyPrimaryFindTest() {
        ManyToManyPrimarySaveTest();
        Long findId = 1L;

        Order order = manager.find(Order.class, findId);
        Member member = order.getMember();
        Product product = order.getProduct();
        System.out.println("member = " + member);
        System.out.println("product = " + product);
        System.out.println("count = " + order.getOrderAmount());
    }


}