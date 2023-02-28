package jpabook.chapter10;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@ToString(exclude = "board")
@Entity
public class BoardDetail {
    @Id
    @GeneratedValue
    @Column(name = "BOARD_DETAIL_ID")
    private Long boardId;

//    @MapsId
//    @OneToOne
//    @JoinColumn(name = "BOARD_ID")
//    private Board board;

    private String content;

    // 연관관계 매핑
//    public void setBoard(Board board) {
//        this.board = board;
//        board.setBoardDetail(this);
//    }
}
