package jpabook.chapter09;

import javax.persistence.Embeddable;

@Embeddable
public class Zipcode {
    private String zip;
    private String plusFour;
}
