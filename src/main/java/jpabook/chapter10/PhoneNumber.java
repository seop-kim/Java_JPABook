package jpabook.chapter10;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
public class PhoneNumber {
    private String areaCode;
    private String localNumber;

    @ManyToOne
    private PhoneServiceProvider provider; // 엔티티 참조
}
