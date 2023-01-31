package jpabook.chapter06;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
public class Member {
    @Id
    @Column(name = "MEMBER_ID")
    private String id;
    private String username;

    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;

//    // 역방향
//    @OneToMany(mappedBy = "member")
//    private List<MemberProduct> memberProducts;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    protected Member() {
    }

    public Member(String id, String username) {
        this.id = id;
        this.username = username;
    }

    public void setTeam(Team team) {
        setTeamValidate();
        this.team = team;
        team.getMembers().add(this);
    }

    private void setTeamValidate() {
        if (this.team != null) {
            this.team.getMembers().remove(this);
        }
    }

//    private void addProduct(Product product) {
//        products.add(product);
//        product.getMembers().add(this);
//    }
}
