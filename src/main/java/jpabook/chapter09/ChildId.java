package jpabook.chapter09;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ChildId implements Serializable {
    private String parentId; // Child.parent와 매핑

    @Column(name = "CHILD_ID")
    private String id;

    public ChildId() {
    }

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
