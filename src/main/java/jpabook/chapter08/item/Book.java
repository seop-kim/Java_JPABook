package jpabook.chapter08.item;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import jpabook.chapter08.item.Item;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("B")
public class Book extends Item {
    private String author;
    private String isbn;
}
