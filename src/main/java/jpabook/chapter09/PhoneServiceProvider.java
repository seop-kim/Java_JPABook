package jpabook.chapter09;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class PhoneServiceProvider {
    @Id
    private String name;
}
