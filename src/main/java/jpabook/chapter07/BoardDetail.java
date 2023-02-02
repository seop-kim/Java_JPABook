package jpabook.chapter07;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = "board")
@Entity
public class BoardDetail {
    @Id
    private Long boardId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "BOARD_ID")
    private Board board;

    private String content;

    // 연관관계 매핑
    public void setBoard(Board board) {
        this.board = board;
        board.setBoardDetail(this);
    }
}
