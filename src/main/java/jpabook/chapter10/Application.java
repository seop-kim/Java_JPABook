package jpabook.chapter10;

import com.mysema.query.Tuple;
import com.mysema.query.jpa.JPASubQuery;
import com.mysema.query.jpa.impl.JPADeleteClause;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.jpa.impl.JPAUpdateClause;
import com.mysema.query.types.Projections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.transaction.Transactional;
import jpabook.chapter10.item.Item;
import jpabook.chapter10.item.Movie;
import jpabook.chapter10.item.QItem;

public class Application {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        Application app = new Application();

        try {
            tx.begin();

            //Todo 비즈니스
            app.createEntity(em);
            String sql = "SELECT ITEM_ID, PRICE, NAME FROM MOVIE WHERE PRICE > ?";
            Query nativeQuery = em.createNativeQuery(sql);
            nativeQuery.setParameter(1, 3000);
            List<Object[]> resultList = nativeQuery.getResultList();

            for (Object[] result : resultList) {
                for (int i = 0; i < result.length; i++) {
                    System.out.println(result[i]);
                }
            }

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();

        } finally {
            em.close();
        }
        emf.close();
    }

    private void createEntity(EntityManager em) {
        Movie movie = new Movie();

        movie.setName("분도의질주");
        movie.setPrice(10000);
        movie.setStockQuantity(100);
        movie.setDirector("분도");
        movie.setActor("태섭");

        em.persist(movie);
        System.out.println("createEntity====");
    }

    private void deleteEntity(EntityManager em) {
        QItem item = QItem.item;

        JPADeleteClause deleteClause = new JPADeleteClause(em, item);
        long delCount = deleteClause
                .where(item.name.eq("분도의질주"))
                .execute();

        System.out.println("delCount = " + delCount);
    }

    private void queryDslEqual(EntityManager em) {
        JPAQuery query = new JPAQuery(em);
        QMember member = new QMember("m");

        query
                .from(member)
                .where(member.name.eq("회원1"))
                .orderBy(member.name.desc())
                .list(member);
    }

    private void queryDslPaging(EntityManager em) {
        JPAQuery query = new JPAQuery(em);
        QItem qItem = QItem.item;

        query
                .from(qItem)
                .where(qItem.price.gt(20000))
                .orderBy(qItem.price.desc(), qItem.stockQuantity.asc())
                .offset(10).limit(20)
                .list(qItem);
    }

    private void queryDslJoin(EntityManager em) {
        JPAQuery query = new JPAQuery(em);
        QOrder order = QOrder.order;
        QOrderItem orderItem = QOrderItem.orderItem;
        QMember member = QMember.member;

        query.from(orderItem)
                .join(order.member, member)
                .leftJoin(order.orderItems, orderItem)
                .list(order);
    }

    private void queryDslLeftJoin(EntityManager em) {
        JPAQuery query = new JPAQuery(em);
        QOrder order = QOrder.order;
        QOrderItem orderItem = QOrderItem.orderItem;

        query.from(order)
                .leftJoin(order.orderItems, orderItem)
                .on(orderItem.count.gt(2))
                .list(order);
    }

    private void queryDslInnerJoin(EntityManager em) {
        JPAQuery query = new JPAQuery(em);
        QOrder order = QOrder.order;
        QOrderItem orderItem = QOrderItem.orderItem;
        QMember member = QMember.member;

        query.from(order)
                .innerJoin(order.member, member).fetch()
                .leftJoin(order.orderItems, orderItem).fetch()
                .list(order);
    }

    private void queryDslFromParamTwo(EntityManager em) {
        JPAQuery query = new JPAQuery(em);
        QOrder order = QOrder.order;
        QMember member = QMember.member;

        query.from(order, member)
                .where(order.member.eq(member))
                .list(order);
    }

    private void queryDslSubQuery1(EntityManager em) {
        JPAQuery query = new JPAQuery(em);
        QItem item = QItem.item;
        QItem subItem = QItem.item;

        query.from(item)
                .where(item.price.eq(
                        new JPASubQuery().from(subItem)
                                .unique(subItem.price.max())
                ))
                .list(item);
    }

    private void queryDslResultTuple(EntityManager em) {
        JPAQuery query = new JPAQuery(em);
        QItem item = QItem.item;

        List<Tuple> result = query.from(item).list(item.name, item.price);

        if (result.isEmpty()) {
            System.out.println("No has data");
        }

        for (Tuple tuple : result) {
            System.out.println("name : " + tuple.get(item.name));
            System.out.println("price : " + tuple.get(item.price));
        }
    }

    private void queryDslResultDTO(EntityManager em) {
        JPAQuery query = new JPAQuery(em);
        QItem item = QItem.item;
        List<ItemDTO> result =
                query
                        .from(item)
                        .list(
                                Projections.bean(ItemDTO.class, item.name.as("username"), item.price)
                        );

        for (ItemDTO itemDTO : result) {
            System.out.println("itemDTO.getUsername() = " + itemDTO.getUsername());
            System.out.println("itemDTO.getPrice() = " + itemDTO.getPrice());
        }
    }

    private void queryDslUpdate(EntityManager em) {
        QItem item = QItem.item;
        JPAUpdateClause updateClause = new JPAUpdateClause(em, item);
        long count =
                updateClause
                        .where(item.name.eq("분도의질주"))
                        .set(item.price, item.price.add(100))
                        .execute();

        System.out.println("count = " + count);
    }
}
