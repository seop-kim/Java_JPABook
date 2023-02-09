package jpabook.chapter08.item;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import jpabook.chapter08.item.Item;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("M")
public class Movie extends Item {
    private String director;
    private String actor;
}
