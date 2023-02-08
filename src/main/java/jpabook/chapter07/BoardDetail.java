package jpabook.chapter07;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
