CHAPTER 07
===
[ 고급 매핑 ]
---


주요 과정
---

| 주제                  | 내용                                                      |
|---------------------|---------------------------------------------------------|
| 상속 관계 매핑            | 객체의 상속 관계를 데이터베이스에 어떻게 매핑하는지 다룬다.                       |
| @MappedSuperclass   | 등록일, 수정일과 같이 여러 엔티티에서 공통으로 사용하는 매핑 정보만 상속 받고자 할 때 사용한다. |
| 복합 키와 식별 관계 매핑      | 데이터베이스의 식별자가 있을 때 매핑하는 방법, 데이터베이스의 식별관계와 비식별 관계를 다룬다.   |
| 조인테이블               | 연관관계를 관리하는 연결테이블을 매핑하는 방법을 다룬다.                         |
| 엔티티 하나에 여러 테이블 매핑하기 | 하나의 엔티티에 다수의 테이블을 매핑하는 방법을 다룬다.                         |

<br><br>

## 상속 관계 매핑

- 관계형 데이터베이스에는 객체지향의 상속과 같은 개념은 없고 ```슈퍼타입 서브타입 관계``` 라는 것이 객체지향의 상속의 개념과 가장 유사하다.  
  ORM에서 이야기하는 상속 관계 매핑은 이 두개의 관계를 매핑하는 것이다.

- 슈퍼타입 서브타입 논리 모델을 실제 물리 모델인 테이블로 구현하는 방법은 3가지가 있다.

  |종류|설명|
              |---|---|
  | 각각의 테이블로 변환 ```(조인전략)``` | 각각을 모두 테이블로 만들고 조회할 때 조인을 사용하는 방법 (JPA에서는 조인 전략이라함)|
  | 통합 테이블로 변환 ```(단일 테이블 전략)```   | 테이블을 하나만 사용하여 모든 컬럼을 합쳐 사용한다 (단일 테이블 전략)|
  | 서브타입 테이블로 변환 ```(구현 클래스별 테이블 전략)``` | 서브 타입마다 하나의 테이블을 만든다. JPA에서는 구현 클래스마다 테이블 전략이라 한다.|

### 조인 전략

- 엔티티 각각을 모두 테이블로 만들고 자식 테이블이 부모 테이블의 기본키를 받아 본인의 키본키 + 외래키로 사용하는 전략이다.  
  조회 시 조인을 자주 사용하게 되며 이 전략을 사용할 때의 주의점은 객체는 타입으로 가능하지만 테이블에는 타입이라는 개념이 없어 타입을 구분하는 컬럼을 추가해야 한다.

<br>

#### 부모객체 Item

```java

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "DTYPE")
public abstract class Item {
    @Id
    @GeneratedValue
    @Column(name = "ITEM_ID")
    private Long id;

    private String name;
    private int price;
}
```

<br>

#### 자식객체 Movie, Book, Album

```java

@Entity
@DiscriminatorValue("M")
public class Movie extends Item {
    private String director;
    private String actor;
}

@Entity
@DiscriminatorValue("B")
public class Book extends Item {
    private String author;
    private String isbn;
}

@Entity
@DiscriminatorValue("A")
public class Album extends Item {
    private String artist;
}
```

|종류|설명|
|---|---|
|@Inheritance(strategy = InheritanceType.JOINED)|상속매핑의 부모 클래스에 사용을 해야하며 매핑 전략을 지정해야 하는데 이 예제에서는 JOIN을 선택하여 JOINED 옵션을 주었다.
|@DiscriminatorColumn(name = "DTYPE")|부모 클래스에 구분 컬럼을 지정하는 어노테이션이다. 이 컬럼을 통해 자식테이블을 구분할 수 있다. ```name 옵션의 기본 값이 DTYPE이다.```|
|@DiscriminatorValue("M")|엔티티 저장 시 구분 컬럼에 입력할 값을 매개변수로 넣는다.|

<br>

#### ID 재정의

```java

@Entity
@DiscriminatorValue("B")
@PrimaryKeyJoinColumn(name = "BOOK_ID") // ID 재정의
public class Book extends Item {
    private String author;
    private String isbn;
}
```

- ID 재정의 : 상속관계의 매핑에서 기본적으로 부모객체의 ID 컬럼명을 그대로 사용한다. 만약 자식 객체의 기본 키 컬럼명을 변경하고자
  한다면 ```@PrimaryKeyJoinColumn(name = "BOOK_ID")```를 사용하면 된다.

<br>

#### 조인 전략 정리

- 장점
    - 테이블이 정규화된다.
    - 외래 키 참조 무결성 제약조건을 활용할 수 있다.
    - 저장공간을 효율적으로 사용한다.


- 단점
    - 조회할 때 조인이 많이 사용되어 성능 저하가 발생할 수 있다.
    - 조회쿼리가 복잡하다.
    - 데이터 저장시 DML(Insert)가 2회 실행된다.


- 특징
    - JPA 표준 명세는 구분 컬럼을 사용하도록 하지만 하이버네이트를 호함한 몇몇 구현체는 구분 컬럼 없이도 동작을 한다.


- 관련 어노테이션
    - @PrimaryKeyJoinColumn
    - @DiscriminatorColumn
    - @DiscriminatorValue

### 단일 테이블 전략

- 단일 테이블 전략은 모든 컬럼을 한 테이블에서 관리하는 것이며 구분 컬럼을 통해 어떤 자식 데이터가 저장되었는지 구분한다.  
  조회 시 조인을 사용하지 않아 일반적으로 가장 빠른 전략이다.

#### 주의점

- 단일 테이블 전략의 주의점은 자식 엔티티가 매핑한 컬럼은 모두 NULL이 허용되어야 한다는 점이다.  
  예로 Book 엔티티를 저장하면 다른 자식 객체의 컬럼들은 사용하지 않게되므로 모두 null로 입력이 되기 때문이다.

<br>

#### 부모객체 Item

```java

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DTYPE")
public abstract class Item {
    @Id
    @GeneratedValue
    @Column(name = "ITEM_ID")
    private Long id;

    private String name;
    private int price;
}
```

- @Inheritance 옵션을 ```SINGLE_TABLE```으로 변경하면 단일테이블 전략을 사용할 수 있다.  
  테이블 하나에 모든 컬럼을 저장하므로 구분 컬럼은 필수로 사용해야 하며 이 전략의 장단점은 하나의 테이블을 사용하는 특징과 관련있다.

<br>

#### 단일테이블 전략 정리

- 장점
    - 조인을 사용하지 않으므로 일반적으로 조회 성능이 좋다.
    - 조회 쿼리가 단순하다.


- 단점
    - 자식 엔티티가 매핑한 컬럼은 모두 null을 허용해야 한다.
    - 단일 테이블에 모든 것을 저장하므로 테이블이 커질 수 있다. 이로 인하여 상황에 따라 성능이 안좋아질 수도 있다.


- 특징
    - 구분 컬럼을 반드시 사용해야 하므로 ```@DiscriminatorColumn(name = "DTYPE")```를 반드시 설정해야 한다.
    - 자식 테이블에서 ```@DiscriminatorValue``` 속성을 사용하지 않으면 엔티티 이름을 사용한다.

### 구현 클래스마다 테이블 전략

- 구현 클래스마다 테이블 전략은 자식 엔티티마다 테이블을 만드는 것이다. 자식 테이블에는 각각에 필요한 모든 컬럼이 있다.
- 여러 자식 테이블을 함께 조회할 때 성능이 좋지 못하고 쿼리가 어렵기에 일반적으로 추천하지 않는 전략이다. ```이 전략은 DB설계자와 ORM전문가 둘 다 추천하지 않는 방법이다.```

<br>

#### 부모객체 Item

```java

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Item {
    @Id
    @GeneratedValue
    @Column(name = "ITEM_ID")
    private Long id;

    private String name;
    private int price;
}
```

<br>

#### 단일테이블 전략 정리

- 장점
    - 서브 타입을 구분해서 처리할 때 효과적이다.
    - not null 제약조건을 사용할 수 있다.


- 단점
    - 여러 자식 테이블을 함께 조회할 때 성능이 느리다. ```SQL에서 UNION을 사용해야 한다.```
    - 자식 테이블을 통합해서 쿼리하기 어렵다.


- 특징
    - 구분 컬럼을 사용하지 않는다.

------

## @MappedSuperclass

- 부모클래스를 테이블과 매핑하지 않고 부모클래스를 상속받은 자식 클래스에게 매핑 정보만 제공하고자 한다면 ```@MappedSuperclass```을 사용하면 된다.  
  이 어노테이션을 사용하면 실제 테이블과 매핑되지 않고 단순히 매핑 정보를 상속하게 된다.

<br>

#### Example

```java

@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
}

@Entity
public class BaseMember extends BaseEntity {
    private String email;
}

@Entity
public class BaseSeller extends BaseEntity {
    private String shopName;
}
```

- BaseEntity에는 객체들이 사용하는 공통 매핑 정보를 정의한다. 자식엔티티들은 이를 상속받아 부모 객체의 매핑 정보를 물려 받게된다.  
  이렇게 될 경우 BaseEntity는 테이블과 매핑할 필요가 없어지므로 ```@MappedSuperclass``` 어노테이션을 통해 테이블과 매핑하지 않게 하면 된다.

<br>

### 부모로부터 물려받은 매핑 정보를 재정의 하는 어노테이션

|어노테이션|설명|
|---|---|
|```@AttributeOverrides``` & ```@AttributeOverride```|부모로부터 물려받은 매핑 정보를 재정의|
|```@AssociationOverrides``` & ```@AssociationOverride```|연관관계 재정의|

#### 사용법

```java

@Entity
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "MEMBER_ID")),
        @AttributeOverride(name = "name", column = @Column(name = "MEMBER_NAME"))
})
public class BaseMember extends BaseEntity {
    private String email;
}
```

<br>

#### @MappedSuperclass 정리

- 테이블과 매핑되지 않고 자식 클래스에 엔티티의 매핑 정보만을 상속하기 위해 사용
- ```@MappedSuperclass```로 지정한 클래스는 엔티티가 아니므로 ```entityManager.find()``` 이나 ```JPQL``` 에서 사용할 수 없다.
- 이 클래스는 직접 생성해서 사용할 일은 거의 없으니 추상 클래스로 만드는 것을 권장한다.
- ```@MappedSuperclass```를 사용하면 등록일자, 수정일자, 등록자, 수정자와 같은 다수의 엔티티에서 공통으로 사용하는 속성을 효과적으로 관리할 수 있다.

<br>

#### 참고사항

- Entity 클래스는 ```Entity```로 선언되어 있거나 ```@MappedSuperclass```로 선언되어 있는 클래스만 상속받을 수 있다.
- ```@MappedSuperclass```는 ORM에서 이야기하는 진정한 상속 매핑이 아니다. ORM에서 이야기하는 상속 매핑은 슈퍼타입 서브타입 관계와 매핑하는 것이다.

------

# 복합 키와 식별 관계 매핑

- 복합 키를 매핑하는 방법과 식별 관계, 비식별 관계에 대해서 매핑하는 방법

<br><br>

## 식별 관계 vs 비식별 관계

- DB 테이블 사이에 관계는 외래 키가 기본 키에 포함된지 여부에 따라 식별 관계와 비식별 관계로 구분된다. 두 관계의 특징을 봐보자

<br>

### 식별 관계

- 식별 관계는 부모 테이블의 기본 키를 내려받아 자식 테이블의 기본키 + 외래키로 사용하는 관계이다.

  | PARENT    |CHILD|
    |-----------|---|
  | ```PARENT_ID(PK)``` |```PARENT_ID(PK,FK)```|
  | ```NAME```       |```CHILD_ID(PK)```|
  |                  |```NAME```|

- 위 관계에서 CHILD는 PARENT_ID를 받아 자신의 기본 키 + 외래 키로 사용한다. 이러한 형태가 식별 관계이다.

<br><br>

### 비식별 관계

- 비식별 관계는 부모 테이블의 기본키를 받아 자식 테이블의 외래키로만 사용하는 관계이다.

  | PARENT                | CHILD                 |
  |-----------------------|-----------------------|
  | ```PARENT_ID(PK)```   | ```CHILD_ID(PK)```    |
  | ```NAME```            | ```PARENT_ID(FK)```   |
  |                       | ```NAME```            |

<br>

- 위 관계에서 CHILD는 부모로 부터 PARENT_ID를 받아와 자신의 외래키로 사용한다. 이러한 관계가 비식별 관계이다.
- 비식별 관계는 외래 키에 NULL을 허용하는지에 따라 필수적 비식별 관계와 선택적 비식별 관계로 나뉜다.

  | 구분          | 설명             |
  |----------------|---------------------------------------|
  | 필수적 비식별 관계  | 외래 키에 NULL을 허용하지 않아 연관관계를 필수적으로 맺어야 함 |
  | 선택적 비식별 관계  | 외래 키에 NULL을 허용하여 연관관계 맺는 것을 선택할 수 있다. |


- 데이터베이스 테이블을 설계할 때 식별 관계나 비식별 관계 중 하나를 선택해야 한다. 최근에는 비식별 관계를 주로 사용하고 꼭 필요한 곳에만 식별 관계를 사용하는 편이다.

<br>

## 복합 키 : 비식별 관계 매핑
- 기본 키를 구성하는 컬럼이 하나면 단순하게 매핑이 가능하지만 둘 이상의 컬럼으로 구성된 경우 별도의 식별자 클래스를 생성해야 한다. ```앞 챕터에서 사용한 @IdClass```
- 식별자 클래스는 ```@IdClass``` 나 ```@EmbeddedId``` 2가지 방법을 사용하는데 ```@IdClass```는 DB에 가까운 방법이고 ```@EmbeddedId```가 객체지향에 가까운 방법이다.

<br>

### @IdClass
- 복합 키 테이블은 비식별 관계이다. 아래를 보자

  |PARENT|CHILD|
  |---|---|
  |PARENT_ID1 (PK)|CHILD_ID (PK)|
  |PARENT_ID2 (PK)|PARENT_ID1 (PK)|
  |NAME|PARENT_ID2 (PK)|
  | |NAME|

- PARENT 테이블을 보면 기본 키를 2개로 묶은 복합 키로 구성했다. 따라서 이를 매핑하기 위해서는 식별자 클래스를 별도로 만들어야 한다.

#### 부모 클래스
```java
@Entity
@IdClass(ParentId.class)
public class Parent {
    @Id
    @Column(name = "PARENT_ID1")
    private String id1;

    @Id
    @Column(name = "PARENT_ID2")
    private String id2;

    private String name;
}
```

<br>

#### 식별자 클래스
```java
public class ParentId implements Serializable {
    private String id1;
    private String id2;

    public ParentId() {

    }

    public ParentId(String id1, String id2) {
        this.id1 = id1;
        this.id2 = id2;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
```

<br>

#### 자식 클래스
```java
@Entity
public class Child {
    @Id
    private String id;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "PARENT_ID1",
                    referencedColumnName = "PARENT_ID1"),
            @JoinColumn(name = "PARENT_ID2",
                    referencedColumnName = "PARENT_ID2")})
    private Parent parent;
}
```

- ```@IdClass```를 사용할 때 식별자 클래스는 다음 조건을 만족해야 한다.
  - 식별자 클래스의 속성명과 엔티티에서 사용하는 식별자의 속성명이 같아야한다.
  - Serializable 인터페이스를 구현해야 한다.
  - 기본 생성자가 있어야 한다.
  - 식별자 클래스는 ```public```이어야 한다.

#### 사용 예제
```java
Parent parent = new Parent();
parent.setId1("myId1");
parent.setId2("myId");
parent.setName("parentName");
em.persist(parent);
```

- 사용 예제를 보면 식별자 클래스인 ParentId가 보이지 않는다. 이는 영속성 컨텍스트에 엔티티가 등록되기 전 내부에서 Parent.id1, Parent.id2 값을 사용하여  
식별자 클래스를 생성하고 영속성 컨텍스트의 키로 사용하기 때문이다. 아래의 조회 예제를 보면 어느정도 감이 올거다.

<br>

```java
ParentId parentId = new ParentId("myId1", "myId2");
Parent parent1 = em.find(Parent.class, parentId);
```

- 조회 코드를 보면 식별자 클래스인 ```ParentId```를 이용하여 엔티티를 조회한다.

<br>

- 자식테이블을 보면 부모 테이블의 기본 키 컬럼이 복합 키 이므로 자식 테이블의 외래 키도 복합 키다. 이에 따라 여러 컬럼을 매핑해야 하여 ```@JoinColumns``` 어노테이션을 사용하여  
각각의 컬럼들을 매핑한다. 자식 클래스의 예제와 같이 ```@JoinColumns``` 와 ```referencedColumnName``` 속성의 값이 같으면 ```referencedColumnName```속성은 생략할 수 있다.

<br>

### @EmbeddedId
- 이 어노테이션은 ```@IdClass```보다 좀 더 객체지향적인 방법이다.
- 사용 예제를 보자

#### 부모 클래스
```java
@Entity
public class Parent {

    @EmbeddedId
    private ParentId id;
    private String name;
}
```

<br>

#### 연결 테이블
```java
@Embeddable
public class ParentId implements Serializable {
    @Column(name = "PARENT_ID1")
    private String id1;
    @Column(name = "PARENT_ID2")
    private String id2;

    public ParentId() {

    }

    public ParentId(String id1, String id2) {
        this.id1 = id1;
        this.id2 = id2;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
```

- ```@IdClass```와는 다르게 ```@EmbeddedId```를 적용할 경우 식별자 클래스에서 기본 키를 직접 매핑한다.
- ```@EmbeddedId```는 아래의 조건이 있다.
  - ```@Embeddable``` 어노테이션을 붙여주어야 한다.
  - ```Serializable``` 인터페이스를 구현해야 한다.
  - ```equals```, ```hashCode```를 구현해야 한다.
  - 기존 생성자가 있어야 한다.
  - 식별자 클래스는 항상 ```public```이어야 한다.

<br>

#### 사용 예제
```java
Parent parent = new Parent();
ParentId parentId = new ParentId("myId1", "myId2");
parent.setId(parentId);
parent.setName("parentName");
em.persist(parent);
```
- ```@EmbeddadId```를 사용하면 식별자 클래스를 직접 생성하여 사용한다.


##### 조회 코드
```java
ParentId parentId = new ParentId("myId1", "myId2");
Parent parent = em.find(Parent.class, parentId);
```
- 조회 코드에서도 ParentId를 직접 사용한다.

<br>

### 복합 키와 equals(), hashCode()
- 복합 키일 경우 ```equals()```와 ```hashCode()```를 반드시 구현해야 한다.

```java
ParentId id1 = new ParentId();
id1.setId1("myId1");
id1.setId2("myId2");

ParentId id2 = new ParentId();
id2.setId1("myId1");
id2.setId2("myId2");

boolean result = id1.equals(id2);
```
- 위 코드에서 ```result```는 ```true```일까 ```false```일까. 오버라이딩을 적절히 하였다면 ```true```일거다.  
자바의 모든 클래스는 기본적으로 ```Object``` 클래스를 상속받는데 이 클래스에서의 ```equals()```메소드는 인스턴스 참조 값 비교인 == 비교를 하기 때문이다.


- 영속성 컨텍스트는 엔티티의 식별자를 키로 이용하여 엔티티를 관리한다. 그리고 식별자를 비교할 때는 ```equals()```와 ```hashCode()```를   
사용하고 객체의 동등성이 지켜지지 않으면 예상과 다른 엔티티가 조회되거나 엔티티를 찾을 수 없는 등 심각한 문제를 일으킨다.

<br>

### @IdClass vs @EmbeddedId
- 이 두 어노테이션은 각각의 장단점이 있어 취향에 맞는 것을 일관성 있게 사용하는 것을 권장한다.  
```@EmbeddedId```가 더 객체지향적이고 중복도 없어서 좋아 보이긴 하지만 특정한 상황에서 JPQL이 조금 더 길어질 수 있다.
- 복합 키에는 ```@GenerateValue```를 어떠한 경우에도 사용할 수 없다.

#### 특정한 경우 
```java
em.createQuery("select p.id.id1, p.id.id2 from Parent p"); // @EmbeddedId
em.createQuery("select p.id1, p.id2 from Parent p"); // @IdClass
```

<br>

### 복합 키: 식별 관계 매핑
| PARENT         | CHILD          | GRANDCHILD         |
|----------------|----------------|--------------------|
| PARENT_ID (PK) | PARENT_ID (PK) | PARENT_ID (PK)     |
| NAME           | CHILD_ID (PK)  | CHILD_ID (PK)      |
| -              | NAME           | GRANDCHILD_ID (PK) |
| -              | -              | NAME               |

- 이번에는 부모, 자식, 손자까지 기본 키를 전달하는 식별 관계를 볼 것이다.  
식별 관계에서 자식 클래스 부터는 기본 키를 포함하여 복합 키를 생성해야 하므로 식별 클래스를 생성하여 매핑해야 한다.

<br>

#### @IdClass와 식별 관계
```java
// 부모
@Entity
public class Parent {
  @Id
  @Column(name = "PARENT_ID")
  private String id;
  private String name;
}

// 자식
@Entity
@IdClass(ChildId.class)
public class Child {
  @Id
  @ManyToOne
  @JoinColumn(name = "PARENT_ID")
  private Parent parent;

  @Id
  @Column(name = "CHILD_ID")
  private String id;
  private String name;
}

// 자식 ID
public class ChildId implements Serializable {
  private String parent; // Child.parent 매핑
  private String childId; // Child.childId 매핑

  // equals, hashCode..
}

// 손자
@Entity
@IdClass(GrandChildId.class)
public class GrandChild {
  @Id
  @ManyToOne
  @JoinColumns({
          @JoinColumn(name = "PARENT_ID"),
          @JoinColumn(name = "CHILD_ID")})
  private Child child;

  @Id
  @Column(name = "GRANDCHILD_ID")
  private String id;
  private String name;
}

// 손자 ID
public class GrandChildId implements Serializable {
  private Child child; // GrandChild.child 매핑
  private String id; // GrandChild.id 매핑
  
  // equals, hashCode..
}
```

- 식별 관계는 기본 키와 외래 키를 같이 매핑해야 한다.  
식별자 매핑인 ```@Id```와 ```@ManyToOne```을 같이 사용하면 된다.  
```Child``` 엔티티의 ```parent```를 보면 ```@Id```로 기본키로 매핑하면서 ```@ManyToOne```과 ```@JoinColumn```으로 외래 키를 같이 매핑한다.

<br><br>

#### @EmbeddedId와 식별 관계
```@EmbeddedId```로 식별 관계를 구성할 떄는 ```@MapsId```를 사용해야 한다.

```java
// 부모
@Entity
public class Parent {
  @Id
  @Column(name = "PARENT_ID")
  private String id;
  private String name;
}

// 자식
@Entity
public class Child {
  @EmbeddedId
  private ChildId id;

  @MapsId("parentId") // ChildId.parentId 매핑
  @ManyToOne
  @JoinColumn(name = "PARENT_ID")
  private Parent parent;
  private String name;
}

// 자식 ID
@Embeddable
public class ChildId implements Serializable {
  private String parentId; // Child.parent와 매핑

  @Column(name = "CHILD_ID")
  private String id;

  // equals, hashCode..
}

// 손자
@Entity
public class GrandChild {
  @EmbeddedId
  private GrandChildId id;

  @MapsId("childId")
  @ManyToOne
  @JoinColumns({
          @JoinColumn(name = "PARENT_ID"),
          @JoinColumn(name = "CHILD_ID")})
  private Child child;

  private String name;
}

// 손자 ID
@Embeddable
public class GrandChildId implements Serializable {
  private ChildId childId; // @MapsId("childId") 매핑

  @Column(name = "GRANDCHILD_ID")
  private String id;

  // equals, hashCode..
}
```

- ```@EmbeddedId```는 식별 관계로 사용할 연관관계의 속성에 ```@MapsId```를 사용하면 된다.
- ```@Id```대신 ```@MapsId```를 사용한다. 이는 외래키와 매핑한 연관관계를 기본키에도 매핑하는 어노테이션이다.   
이 어노테이션의 속성 값은 ```@EmbeddedId```를 사용한 식별자 클래스의 기본 키 필드를 지정하면 된다.

<br>
 
#### 궁금한 점
- ```Child```와 ```GrandChild```를 보면 ```ChildId```에서는 ```parentId```를 그냥 String 타입으로 값을 저장하고 ```Child```에서는 ```Parent```객체로 값을 저장한다.     
```GrandChildId```에서는 ```ChildId``` 객체로 값을 저장하고 ```GrandChild```도 ```Child``` 객체로 값을 저장한다. 물론 String도 객체이지만 왜 이럴까?  
부모 객체에서 자식 객체로 갈때는 PK값을 자식에 저장하면서 복합키로 설정이 되고 손자 객체에서는 자식 객체에 이미 복합키로 되어 있는 값을 저장하기에 이러는 걸까?  
  - 복합키를 복합키로 저장하기 위해서 사용된 것으로 보인다. 내가 생각한 방법으로 해봤는데 참조하지 못하는 에러가 발생하면서 실행이 되지 않는다.
  - 생각한 것은 ```GrandChildId```에서도 객체가 아닌 ```Child```의 ```Id``` 값을 ```String```으로 저장하는거다. 이에 맞게 객체를 수정해 봤지만 에러가 발생한다.  
  복합키를 자식 객체의 복합키로 설정을 하기 위해서는 위의 방법으로 해야하는 것 같다. 이해가 엄청 잘 되지는 않지만 어찌되었건 위의 방식으로 사용해야 된다는 것인 것 같다..허허.
    - 하긴.. 만약 위와 같이 되었다면 손자 테이블은 ```PARENT_ID``` 컬럼을 가지고 있지 않겠구나..
    - 근데 위에 처럼 되었으면 식별 클래스를 만드는 것이 획일화되어 사용이 좀 더 편하지 않았을까..

<br>

#### 해결
- 궁금한 점에서 생각했던 방식은 PK로 등록하는 것을 고려하지 않았었다. 복합키를 가지고 있는 자식객체를 손자에게 넘겨줄 때 부모 객체의 Id까지 넘기기 위해서는 ```ChildId```를 사용해야 한다..

<br><br>

### 비식별 관계로 구현
| PARENT         | CHILD           | GRANDCHILD         |
|----------------|-----------------|--------------------|
| PARENT_ID (PK) | CHILD_ID (FK)   | GRANDCHILD_ID (PK) |
| NAME           | PARENT_ID (FK)  | CHILD_ID (FK)      |
| -              | NAME            | NAME               |

#### 예제
```java
// 부모
@Entity
public class Parent {
  @Id
  @GeneratedValue
  @Column(name = "PARENT_ID")
  private Long id;
  private String name;
}

// 자식
@Entity
public class Child {
  @Id
  @GeneratedValue
  @Column(name = "CHILD_ID")
  private Long id;
  private String name;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "PARENT_ID")
  private Parent parent;
}

// 손자
@Entity
public class GrandChild {
  @Id
  @GeneratedValue
  @Column(name = "GRANDCHILD_ID")
  private Long id;
  private String name;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "CHILD_ID")
  private Child child;
}
```
- 이 전 예제보다 코드가 쉽고 단순하다. 복합키가 없으므로 식별자 클래스를 만들지 않아도 된다.

<br><br>

### 일대일 식별 관계
- 일대일 식별 관계는 자식테이블의 기본 키 값으로 부모 테이블의 기본 키 값만 사용한다.

#### 객체 예제
```java
@Entity
public class Board {
    @Id
    @GeneratedValue
    @Column(name = "BOARD_ID")
    private Long id;

    private String title;

    @OneToOne(mappedBy = "board")
    private BoardDetail boardDetail;
}

@ToString(exclude = "board")
@Entity
public class BoardDetail {
  @Id
  private Long boardId;

  @MapsId
  @OneToOne
  @JoinColumn(name = "BOARD_ID")
  private Board board;

  private String content;

  // 연관관계 매핑
  public void setBoard(Board board) {
    this.board = board;
    board.setBoardDetail(this);
  }
}
```
- 이번 예제에는 ```Lombok```의 ```@ToString```을 보이게 했다.  
만약 위 클래스를 만들고 ```@ToString```을 고치지 않고 객체 print하면 stack overflow 에러가 발생한다.  
```@ToString```에서 서로의 객체를 반복해서 호출을 하여 발생하는 문제이니 서로가 반복해서 호출되는 문제를 처리하고 프린트하자.

<br>

#### 일대일 식별과제 예제 실행 코드
```java
Board board = new Board();
board.setTitle("제목");
manager.persist(board);

BoardDetail boardDetail = new BoardDetail();
boardDetail.setContent("내용");
boardDetail.setBoard(board);
manager.persist(boardDetail);
```

<br><br>

### 식별, 비식별 관계의 장단점
- DB 설계 관점에서는 아래와 같은 이유로 비식별 관계를 선호한다.
  - 식별관계는 자식테이블로 전파가 되면 뎁스 당 기본키가 1개씩 늘어나 JOIN 사용 시 쿼리가 복잡해지고 기본 키 인덱스가 불필요하게 커진다.
  - 식별관계는 2개 이상의 컬럼을 합해 기본 키를 만들어야하는 경우가 많다.
  - 식별관계는 기본 키를 비즈니스 의미가 있는 자연키를 컬럼을 조합하는 경우가 많다. 시간이 지남에 따라 요구사항이 변경될 경우 식별관계의 기본키는 자식에 손자까지 전파되며 수정이 힘들어진다.  
  반면 비식변 관계는 비즈니스와는 전혀 관계가 없는 대리 키를 사용한다.
  - 식별관계는 비식별관계보다 테이블의 구조가 유연하지 못하다.


- 객체 관계 매핑 관점에서는 비식별 관계를 선호한다.
  - 식별관계를 매핑하기 위해서는 일대일 관계를 제외하고는 식별자 클래스를 생성해서 사용해야하여 비식별 관계 매핑보다 더 많은 노력이 필요하다.
  - ```@GenerateValue```를 사용해 대리키로 되어 있는 기본키를 자동 생성되게 할 수 있다.

<br>

- 식별관계의 장점은 기본 키 인덱스를 활용하기 좋고 자식테이블, 손자테이블들이 상위 테이블의 기본키를 가지고 있어 특정 상황에서 조인 없이 테이블만으로 검색이 가능하다.  
  식별관계도 비식별관계에는 없는 장점이 있으므로 꼭 필요한 곳에는 적절하게 사용하는 것이 테이블 설계의 묘를 살리는 방법이다.

<br><br>
------
## 조인테이블
- 데이터베이스 테이블의 연관관계를 설정하는 방법은 크게 2가지가 있다.
  - 조인 컬럼 사용 (외래키)
  - 조인 테이블 사용 (테이블 사용)

  - 조인 컬럼 사용
  
  | MEMBER                   | LOCKER         |
  |--------------------------|----------------|
  | MEMBER_ID (PK)           | LOCKER_ID (PK) |
  | USERNAME                 | NAME           |
  | LOCKER_ID (FK, Null허용)  |                |

    - 조인 컬럼은 테이블 간에 관계를 외래 키 컬럼을 사용해서 관리한다.
    - 위 테이블 구조에서 볼 때 만약 멤버가 락커를 필요로 하지 않는다면 락커 컬럼에 ```null``` 저장하면 된다. 이렇게 ```null```값을 허용하는 것을 선택적 비식별 관계라고 한다.  
      이렇게 ```null```을 허용하는 경우 회원과 락커를 조인할 때에는 ```outer join```을 사용해야 한다. ```inner join```을 사용할 경우 멤버의 ```LOCKER_ID```가 ```null```일 경우 회원은 조회가 되지 않는다.

<br>

  - 조인 테이블 사용
  
    | MEMBER          | MEMBER_LOCKER   | LOCKER          |
    |-----------------|-----------------|-----------------|
    | MEMBER_ID (PK)  | MEMBER_ID (FK)  | LOCKER_ID (PK)  |
    | USERNAME        | LOCKER_ID (FK)  | NAME            |

    - 조인 테이블 사용시 위와 같은 구조가 된다. (ManyToMany에서 보던 구조와 같다.)  
    - 이 방법은 별도의 조인 테이블이라는 테이블을 사용하여 연관관계를 관리한다.  
    - 조인테이블에서 두 테이블의 외래키를 가지고 연관관계를 관리하기에 각 테이블에는 외래키가 존재하지 않는다.  
    - 이 방법의 단점은 관리해야하는 테이블이 늘어나고 두 테이블을 조인하기 위해서는 조인테이블까지 조인해야 한다. 기본적으로 조인 컬럼을 사용하고 필요하다는 판단이 되면 조인 테이블을 사용하자  

<br><br>

- 객체와 테이블을 매핑할 때 조인 컬럼은 ```@JoinColumn```으로 매핑하고 조인테이블을 ```@JoinTable```로 매핑한다.
- 조인 테이블은 주로 다대다 관계를 일대다, 다대일로 풀어내기 위해 사용한다. 그렇지만 일대일, 일대다, 다대일 관계에서도 사용한다.
- 조인 테이블은 ```연결테이블```, ```링크 테이블```이라고도 부른다



### 일대일 조인 테이블
- 일대일 관계를 만들기 위해는 조인 테이블의 외래 키 컬럼 각각에 총 2개의 유니크 제약조건을 걸어야 한다.

```java
// 부모
@Entity
public class Parent {
    @Id
    @GeneratedValue
    @Column(name = "PARENT_ID")
    private Long id;
    private String name;

    @OneToOne
    @JoinTable(
            name = "PARENT_CHILD",
            joinColumns = @JoinColumn(name = "PARENT_ID"),
            inverseJoinColumns = @JoinColumn(name = "CHILD_ID"))
    private Child child;
}

// 자식
@Entity
public class Child {
  @Id
  @GeneratedValue
  @Column(name = "CHILD_ID")
  private Long id;
  private String name;
}
```

  - 부모 엔티티를 보면 ```@JoinColumn``` 대신 ```@JoinTable```을 사용하여 매핑을 하였다.
    - ```@JoinTable``` 속성
      - name : 매핑할 조인 테이블 이름
      - joinColumns : 현재 엔티티를 참조하는 외래 키
      - inverseJoinColumns : 반대방향 엔티티를 참조하는 외래 키

    - 양방향 매핑을 하려면 ```Child```에 아래 코드를 추가하면 된다.
      ```java
      @OneToMany(mappedBy = "child")
      private Parent parent;
      ```
      
<br><br>

### 일대다 조인 테이블
- 일대다 관계를 만들기 위해서는 조인 테이블의 컬럼 중 다와 관련된 컬럼인 ```CHILD_ID```에 유니크 제약조건을 걸어야 한다.

```java
// 부모
@Entity
public class Parent {
    @Id
    @GeneratedValue
    @Column(name = "PARENT_ID")
    private Long id;
    private String name;

    @OneToMany
    @JoinTable(
            name = "PARENT_CHILD",
            joinColumns = @JoinColumn(name = "PARENT_ID"),
            inverseJoinColumns = @JoinColumn(name = "CHILD_ID"))
    private List<Child> child = new ArrayList<>();
}

// 자식
@Entity
public class Child {
  @Id
  @GeneratedValue
  @Column(name = "CHILD_ID")
  private Long id;
  private String name;
}
```
  - 일대일 관계의 부모객체만 어노테이션과 객체만 변경해주면 된다.

<br><br>

### 다대일 조인 테이블
- 다대일은 일대다에서 방향만 반대이므로 조인 테이블 모양은 일대다와 동일하다.
  - ```@ManyToOne```의 ```optional``` 속성은 ```@JoinColumn```의 ```nullable``` 속성이 ```false```인 경우 ```parent```없는 ```child```는 없다는 제약조건을 만드는 것이다.  
    제약조건이 생기므로 일반적으로 ```Outer Join``` 보다 빠른 ```Inner Join```을 사용할 수 있다.

```java
// 부모
@Entity
public class Parent {
    @Id
    @GeneratedValue
    @Column(name = "PARENT_ID")
    private Long id;
    private String name;

    @OneToMany(mappedBy = "parent")
    private List<Child> child = new ArrayList<>();
}

// 자식
@Entity
public class Child {
  @Id
  @GeneratedValue
  @Column(name = "CHILD_ID")
  private Long id;
  private String name;

  @ManyToOne(optional = false)
  @JoinTable(
          name = "PARENT_CHILD",
          joinColumns = @JoinColumn(name = "CHILD_ID"),
          inverseJoinColumns = @JoinColumn(name = "PARENT_ID"))
  private Parent parent;
}
```

<br><br>

### 다대다 조인 테이블
- 다대다 관계를 만들려면 조인 테이블의 두 컬럼을 합해서 하나의 복합 유니크 제약조건을 걸어야 한다. (```PARENT_ID```, ```CHILD_ID``` 는 복합 기본키이므로 유니크 제약조건이 걸려있다.)
- ```@JoinTable```에서는 ```@Column```을 사용할 수 없다.

```java
// 부모
@Entity
public class Parent {
  @Id
  @GeneratedValue
  @Column(name = "PARENT_ID")
  private Long id;
  private String name;

  @ManyToMany
  @JoinTable(
          name = "PARENT_CHILD",
          joinColumns = @JoinColumn(name = "PARENT_ID"),
          inverseJoinColumns = @JoinColumn(name = "CHILD_ID"))
  private List<Child> child = new ArrayList<>();
}

// 자식
@Entity
public class Child {
  @Id
  @GeneratedValue
  @Column(name = "CHILD_ID")
  private Long id;
  private String name;

  @ManyToMany(mappedBy = "child")
  private List<Parent> parents = new ArrayList<>();
}
```

<br><br>


## 엔티티 하나에 여러 테이블 매핑
- 잘 사용하지는 않지만 ```@SecondaryTable```을 사용하면 한 엔티티에 여러 테이블을 매핑할 수 있다.

```java
@Entity
@Table(name = "Board")
@SecondaryTable(
        name = "BOARD_DETALL",
        pkJoinColumns = @PrimaryKeyJoinColumn(name = "BOARD_DETAIL_ID"))
public class Board {
    @Id
    @GeneratedValue
    @Column(name = "BOARD_ID")
    private Long id;

    private String title;

    @Column(table = "BOARD_DETAIL")
    private String content;
}
```

- ```Board``` 엔티티는 ```@Table```을 사용하여 테이블과 매핑을 하였다. 그리고  
  ```@SecondaryTable``` 속성을 사용하여 ```BOARD_DETAIL``` 테이블과도 매핑을 했다.


- ```@SecondaryTable```의 속성
  - ```name``` : 매핑할 다른 테이블의 이름, 예제에서는 테이블명을 ```BOARD_DETAIL```로 지정했다. 
  - ```pkJoinColumns``` : 매핑할 다른 테이블의 기본 키 컬럼 속성, 에제에서는 기본 키 컬럼명을 ```BOARD_DETAIL_ID```로 지정했다


- ```content```필드는 ```@Column(table = "BOARD_DETAIL)```을 사용하여 테이블과 매핑하였다.  
  ```title```필드처럼 테이블을 지정하지 않으면 기본 테이블인 ```BOARD```에 매핑된다.


- 더 많은 테이블을 매핑하려면 ```@SecondaryTables```를 사용하면 된다.


- ```@SecondaryTable```은 항상 두 테이블을 조회하기에 최적화를 하기 힘들어 이 방법보다는 일대일 매핑을 하는 것을 추천한다.  
  






