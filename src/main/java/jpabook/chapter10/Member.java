package jpabook.chapter10;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.QueryHint;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@NamedQueries({
        @NamedQuery(
                name = "Member.findByUsername",
                query = "select m from Member m where m.name = :name"
        ),
        @NamedQuery(
                name = "Member.count",
                query = "select count(m) from Member m"
        )
})
public class Member {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private int age;


    @Embedded
    private Address homeAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "city", column = @Column(name = "COMPANY_CIYT")),
            @AttributeOverride(name = "street", column = @Column(name = "COMPANY_STREET")),
            @AttributeOverride(name = "state", column = @Column(name = "COMPANY_STATE")),
            @AttributeOverride(name = "zipcode.zip", column = @Column(name = "COMPANY_ZIP")),
            @AttributeOverride(name = "zipcode.plusFour", column = @Column(name = "COMPANY_PLUS_FOUR"))})
    private Address companyAddress;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();
}

//    public void setTeam(Team team) {
//        setTeamValidate();
//        this.team = team;
//        team.getMembers().add(this);
//    }
//
//    private void setTeamValidate() {
//        if (this.team != null) {
//            this.team.getMembers().remove(this);
//        }
//    }
