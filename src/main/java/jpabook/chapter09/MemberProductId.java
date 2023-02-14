package jpabook.chapter09;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberProductId implements Serializable {
//    private String member; // MemberProduct.member 와 연결
//    private String product; // MemberProduct.product 와 연결
//
//    // hashCode and equals
//
//    @Override
//    public boolean equals(Object obj) {
//        return super.equals(obj);
//    }
//
//    @Override
//    public int hashCode() {
//        return super.hashCode();
//    }
}
