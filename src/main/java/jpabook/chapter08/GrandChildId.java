package jpabook.chapter08;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class GrandChildId implements Serializable {
    private String childId; // @MapsId("childId") 매핑

    @Column(name = "GRANDCHILD_ID")
    private String id;

    // equals, hashCode
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

