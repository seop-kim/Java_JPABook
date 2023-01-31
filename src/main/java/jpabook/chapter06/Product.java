package jpabook.chapter06;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
public class Product {

    @Id
    @Column(name = "PRODUCT_ID")
    private String id;
    private String name;
}
