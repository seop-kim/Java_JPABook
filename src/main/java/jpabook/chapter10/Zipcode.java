package jpabook.chapter10;

import javax.persistence.Embeddable;

@Embeddable
public class Zipcode {
    private String zip;
    private String plusFour;
}
