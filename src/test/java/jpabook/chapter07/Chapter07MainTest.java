package jpabook.chapter07;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Chapter07MainTest {
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
        Board board = new Board();
        board.setTitle("제목");
        manager.persist(board);

        BoardDetail boardDetail = new BoardDetail();
        boardDetail.setContent("내용");
        boardDetail.setBoard(board);
        manager.persist(boardDetail);

        transaction.commit();

        Board findBoard = manager.find(Board.class, 1L);
        System.out.println("findBoard = " + findBoard);

        BoardDetail findBoardDetail = manager.find(BoardDetail.class, 1L);
        System.out.println("findBoardDetail = " + findBoardDetail);


    }

}