package jpabook.chapter05;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
//@Entity
public class Team {
    @Id
    @Column(name = "TEAM_ID")
    private String id;
    private String name;

    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();

    protected Team() {
    }

    public Team(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
