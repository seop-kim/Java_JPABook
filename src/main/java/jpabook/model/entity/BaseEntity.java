package jpabook.model.entity;

import java.util.Date;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class BaseEntity {
    private Date createDate;
    private Date lastModifiedDate;
}
