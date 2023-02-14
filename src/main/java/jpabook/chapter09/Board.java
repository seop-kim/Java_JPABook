package jpabook.chapter09;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "Board")
public class Board {
    @Id
    @GeneratedValue
    @Column(name = "BOARD_ID")
    private Long id;

    private String title;

//    @Column(table = "BOARD_DETAIL")
    private String content;

//    @OneToOne(mappedBy = "board")
//    private BoardDetail boardDetail;
}
