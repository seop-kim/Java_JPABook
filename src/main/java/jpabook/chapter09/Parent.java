package jpabook.chapter09;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
public class Parent {
    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "parent"
            , fetch = FetchType.LAZY
            , cascade = CascadeType.ALL
            , orphanRemoval = true)
    private List<Child> children = new ArrayList<>();

//    private String name;
//    @ManyToMany
//    @JoinTable(
//            name = "PARENT_CHILD",
//            joinColumns = @JoinColumn(name = "PARENT_ID"),
//            inverseJoinColumns = @JoinColumn(name = "CHILD_ID"))
//    private List<Child> child = new ArrayList<>();
}
