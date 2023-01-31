package jpabook.chapter06;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@Entity
//@IdClass(MemberProductId.class)
public class MemberProduct {
//
//    @Id
//    @ManyToOne
//    @JoinColumn(name = "MEMBER_ID")
//    private Member member;
//
//    @Id
//    @ManyToOne
//    @JoinColumn(name = "PRODUCT_ID")
//    private Product product;
//
//    private int orderAmount;
}
