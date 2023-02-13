CHAPTER 08
===
[ 프록시와 연관관계 관리 ]
---


주요 과정
---

| 주제                 | 내용                                                                                                                                                                                                                                 |
|--------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 프록시와 즉시로딩, 지연로딩    | 객체는 객체그래프를 통해 데이터를 조회할 수 있다. 다만 실제 데이터는 데이터베이스에 저장되어 있으므로 테이블의 데이터를 조회해야 객체 그래프로 탐색이 가능하다.<br/> JPA는 이러한 문제를 프록시라는 기술을 사용하여 연관된 데이터를 실제 사용하는 시점에 데이터베이스에 조회할 수 있게 해두었는데 <br/>이 기능은 자주 사용하는 객체들에 대해서 조인을 통해 데이터를 미리 조회하는 것을 가능케 한다. |
| 영속성 전이와 고아객체       | 연관된 객체를 함께 저장하거나 삭제할 수 있는 기능                                                                                                                                                                                                       |

## 프록시

- 엔티티를 조회할 때 연관관계의 엔티티들이 항상 사용되는 것이 아니다. 예로 회원 예제를 사용할 때 비즈니스 로직에 따라 팀 객체가 필요할 수도, 필요 없을 수도 있다.
- 예제 코드를 봐보자

<br>

### 회원과 팀 엔티티

```java

@Entity
public class Member {
    @Id
    @Column(name = "MEMBER_ID")
    private String id;
    private String username;

    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;
}

@Entity
public class Team {
    @Id
    @Column(name = "TEAM_ID")
    private String id;
    private String name;
}
```

### 회원과 팀의 정보를 출력하는 로직

```java
@Test
public void printUserAndTeam(String memberId){
        Member findMember=manager.find(Member.class,memberId);
        Team findTeam=findMember.getTeam();
        System.out.println("findMember = "+findMember);
        System.out.println("findTeam = "+findTeam);
        }
```

### 회원 정보만 출력하는 로직

```java
@Test
public void printUser(String memberId){
        Member findMember=manager.find(Member.class,memberId);
        System.out.println("findMember = "+findMember);
        }
```

- 위 로직을 보면 유저만을 조회하는 예제와 유저와 팀을 조회하는 예제가 있다.  
  유저만을 조회하는 로직에서 팀 객체까지 조회를 하는 것은 효율적이지 않아 JPA에서는 이런 문제를 해결하고자 엔티티가 실제 사용될 때까지 조회를 지연하는 방법을 제공하는데 이를 지연로딩 이라고 한다.  
  지연로딩을 사용하기 위해서는 실제 엔티티 객체 대신 데이터베이스 조회를 지연할 수 있는 가짜 객체가 필요한데 이를 ```프록시객체```라고 한다.

    - 하이버네이트는 지연 로딩을 지원하기 위해 프록시를 사용하는 방법과 바이트코드를 수정하는 두 가지 방법을 제공하지만 바이트코트를 수정하는 방법은 설정이 복잡하므로  
      책에서는 따로 소개를 하지 않는다. 바이트코드를 수정하는 방법은 하이버네이트 공식 사이트를 참고하자.

<br><br>

### 프록시 기초

- JPA에서 식별자를 통해 엔티티를 조회하려면 ```EntityManager.find()```를 사용한다. 이 메소드는 영속성 컨텍스트에 엔티티가 없으면 데이터베이스를 조회하게 된다.  
  이는 호출하게 되면 실제 사용과는 무관하게 데이터베이스에서 값을 가져오게 되는데 만약 엔티티를 실제 사용하는 시점까지 데이터베이스 조회를 미루고자
  한다면 ```EntityManager.getReference()```메소드를 사용하면 된다.  
  이 메소드를 호출하면 JPA는 데이터베이스를 조회하지 않고 실제 엔티티 객체를 생성하지도 않는다. 대신 데이터베이스 접근을 위임한 프록시 객체를 반환한다.

#### 프록시의 특징

- 프록시 객체는 실제 클래스를 상속 받아 만들어지므로 실제 클래스와 겉 모양이 같다. 따라서 사용자는 이것이 진짜 객체인지 구분하지 않고 사용하면 된다.

- 프록시 객체는 실제 객체의 참조 값을 보관한다. 프록시 객체의 메소드가 호출되면 프록시 객체는 실제 객체의 메소드를 호출을 한다.

#### 프록시 객체의 초기화

- 프록시 객체는 메소드가 호출되는 시점에 데이터베이스를 조회하여 엔티티 객체를 생성하는데 이를 ```프록시 객체의 초기화```라고 한다.

<br>

##### 프록시 초기화 예제

```java
Member member=manager.getReference(Member.class,memberId);
        member.getUsername();
```

##### 프록시 클래스 예상 코드

```java
class MemberProcy extends Member {
    Member target = null; // 실제 엔티티 참조

    public String getName() {
        if (target == null) {
            // 초기화 요청
            // DB조회
            // 실제 엔티티 생성 및 참조 보과
            this.target = ...;
        }

        return target.getNmae();
    }
}
```

<br>

#### 프록시의 특징

- 프록시 객체는 처음 사용할 때 한 번만 초기화된다.
- 프록시 객체를 초기화한다고 프록시 객체가 실제 엔티티로 바뀌는 것은 아니다. 프록시 객체가 초기화되면 프록시 객체를 통해서 실제 엔티티에 접근할 수 있다.
- 프록시 객체는 원본 엔티티를 상속받은 객체이므로 타입 체크 시에 주의해서 사용해야 한다.
- 영속성 컨텍스트에 찾는 엔티티가 이미 있으면 데이터베이스를 조회할 필요가 없으므로 ```em.getReference()```를 호출해도 프록시가 아닌 실제 엔티티가 반환된다.
- 초기화는 영속성 컨텍스트의 도움을 받아야 가능한다. 따라서 영속성 컨텍스트의 도움을 받을 수 없는 준영속 상태의 프록시를 초기화하면 문제가 발생한다.

<br>

#### 준영속 상태와 초기화

- 준영속 상태와 초기화에 관련된 코드는 다음과 같다.

  ```java
  // MemberProxy 반환
  Member member = em.getReference(Member.class, memberId);
  transaction.commit();
  em.close();
  
  member.gerName(); // 준 영속 상태의 엔티티를 초기화 시도하므로 예외가 발생한다.
  ```

<br><br>

### 프록시와 식별자

- 엔티티를 프록시로 조회할 때 식별자 값을 파라미터로 전달하는데 프록시 객체는 이 식별자 값을 보관한다.   
  그러니 만약 프록시로 가짜 객체를 생성하고 ```getId()```를 호출하게 되면 이미 프록시 객체가 식별자 값을 가지고 있으므로 프록시는 초기화되지 않는다.  
  다만 조건은 엔티티 접근 방식이 ```@Access(AccessType.PROPERTY)```로 설정되어 있을 경우에만 초기화 되지 않는다.  
  엔티티 접근 방식을 ```@Access(AccessType.FIELD)```로 설정하면 JPA는 ```getId()```매소드가 id만 조회하는 메소드인지 다른 필드까지 활용해서 어떤 일을 하는 메소드인지 알지
  못하므로 프록시 객체를 초기화 한다.


- 프록시는 다음 코드와 같이 연관관계를 설정할 때 유용하게 사용 가능하다.
  ```java
  Member member = em.find(Member.class, "member1");
  Team team = em.getReference(Team.class, "team1"); // SQL을 실행하지 않음
  member.setTeam(team);
  ```

    - 연관관계를 설정할 때는 식별자 값만 사용하므로 프록시를 사용하면 데이터베이스 접근 횟수를 줄일 수 있다.   
      참고로 연관관계를 설정할 때는 엔티티 접근 방식을 필드로 설정해도 프록시를 초기화하지 않는다.

<br>

### 프록시 확인

- JPA가 제공하는 ```PersistenceUnitUtil.isLoaded(Object entity)``` 메소드를 사용하면 프록시 인스턴스의 초기화 여부를 확인할 수 있다.  
  초기화 되지 않은 경우 ```false```를 반환하고 초기화가 되었거나 프록시 인스턴스가 아니면 ```true```를 반환한다.

- 조회한 엔티티가 진짜 엔티티인지 프록시로 조회한 것인지 확인하려면 클래스명을 직접 출력해보면 된다.  
  클래스명 뒤에 ```..javassist..```라 되어 있으면 프록시로 조회한 객체이다. 이 값은 프록시를 생성하는 라이브러리에 따라 출력 결과는 달라질 수 있다.

<br>

#### 프록시 강제 초기화

- 하이버네이트의 ```initialize()```메소드를 사용하면 프록시를 강제로 초기화 할 수 있다.
  ```java
  org.hibernate.Hibernate.initialize(order.getMember()); // 프록시 초기화
  ```
    - JPA 표준에는 프록시 강제 초기화 메소드가 없다. 그러므로 프록시를 강제로 초기화하고자 한다면 ```getName()```과 같은 프록시 메소드를 직접 호출하면 된다.

<br><br>

## 즉시 로딩과 지연 로딩

- 프록시 객체는 주로 연관된 엔티티를 지연 로딩할 때 사용한다.
- JPA는 개발자가 연관된 엔티티의 조회 시점을 선택할 수 있도록 두 가지 방법을 제공한다.
    - 즉시로딩
        - 엔티티를 조회할 때 연관된 엔티티도 함께 조회한다.
        - @ManyToOne(fetch = FetchType.EAGER)

    - 지연로딩
        - 연관된 엔티티를 실제 사용할 때 조회한다.
        - @ManyToOne(fetch = FetchType.LAZY)

<br>

### 즉시 로딩

- 즉시로딩을 사용하려면 연관관계 어노테이션 ```@ManyToOne``` 의 속성에 ```fetch``` 속성을 ```EAGER```로 지정한다.

  ```java
  @Entity
  public class Member {
    @Id
    @Column(name = "MEMBER_ID")
    private String id;
    private String username;
  
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TEAM_ID")
    private Team team;
  }
  
  // 실행코드
  @Test
  void print() {
    Member member = manager.find(Member.class, "member1");
    Team team = member.getTeam();
    System.out.println("team = " + team);
  }
  ```
    - 위 예제는 즉시로딩으로 실행코드에서 ```member```를 ```find```할 때 ```team```도 함꼐 조회된다.
    - 대부분의 JPA 구현체는 즉시 로딩을 최적화하기 위해 가능하면 조인 쿼리를 사용하여 데이터를 조회한다.

  ```jpaql
    select
        member0_.member_id as member_i1_9_0_,
        member0_.team_id as team_id3_9_0_,
        member0_.username as username2_9_0_,
        team1_.team_id as team_id1_15_1_,
        team1_.name as name2_15_1_ 
    from
        member member0_ 
    left outer join
        team team1_ 
            on member0_.team_id=team1_.team_id 
    where
        member0_.member_id=?
  ```

<br>

- 사용된 SQL을 보면 외부 조인을 사용하고 있다.  
  이는 회원테이블의 ```TEAM_ID```가 ```null```값을 허용하기에 팀에 소속되지 않은 회원이 있을 가능성이 있어  
  JPA가 위와 같은 상황을 고려하여 외부 조인을 사용한 것이다.  
  허나 외부 조인보다는 내부 조인이 성능과 최적화에서 더 유리하다. 내부 조인으로 SQL이 실행되기를 바란다면 외래키에 ```NOT NULL``` 제약 조건을 설정하면 된다.  
  JPA에서는 ```@JoinColumn```에 ```nullable = false```을 설정하면 된다.

  ```java
  @Entity
  public class Member {
    @Id
    @Column(name = "MEMBER_ID")
    private String id;
    private String username;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TEAM_ID",
              nullable = false)
    private Team team;
  }
  ```
  <br>

#### nullable 설정에 따른 조인 전략

- ```@JoinColumn(nullable = true)``` 는 null 값을 허용하므로 외부 조인을 사용한다.
- ```@JoinColumn(nullable = false)``` 는 null 값을 허용하지 않음으로 내부 조인을 사용한다.
- ```@ManyToOne(optional = false)```를 설정해도 내부 조인을 사용한다.


- 정리하자면 JPA는 선택적 관계에서는 외부조인을 필수 관계라면 내부 조인을 사용한다.

### 지연 로딩

  ```java

@Entity
public class Member {
    @Id
    @Column(name = "MEMBER_ID")
    private String id;
    private String username;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    private Team team;
}

    // 실행코드
    @Test
    void print() {
        Member member = manager.find(Member.class, "member1");
        Team team = member.getTeam();
    }
  ```

- 지연로딩을 설정하게 되면 ```member```를 호출하는 과정에서 ```team```은 조회하지 않고 대신 프록시 객체를 넣어둔다.  
  이 프록시 객체는 실제로 사용될 때 까지 데이터 조회를 미룬다. 그래서 지연 로딩이라고 한다.


- 조회 대상이 영속성 컨텍스트 영역에 이미 존재한다면 프록시 객체를 사용할 이유가 없다.  
  이때는 실제 객체를 사용한다.

<br>

### 증시로딩 지연로딩 정리

- 지연로딩과 즉시로딩 중 어느 하나만 사용한다는 것은 좋은 선택은 아니다.  
  필요에 따라 로딩 방식을 설정하여 능동적으로 구현을 해야한다.

<br><br>

## 지연로딩 활용

![img1](../../../../var/folders/8z/7dpb98794zg0074h9wt7lgnm0000gn/T/TemporaryItems/NSIRD_screencaptureui_JJ7RAZ/스크린샷 2023-02-10 오전 11.12.03.png)

- 위와 같은 모델이 있다고 해보자
    - 회원은 팀 하나에만 속할 수 있다.
    - 회원은 여러 주문내역을 가진다.
    - 주문내역은 상품정보를 가진다.

- 애플리케이션 로직은 다음과 같다.
    - ```Member```와 연관된 ```Team```은 자주 함께 사용되었다. 그래서 ```Member```와 ```Team```은 즉시 로딩으로 설정했다.
    - ```Member```와 연관된 ```Order```는 가끔 사용되었다. 그래서 ```Member```와 ```Order```는 지연 로딩으로 설정했다.
    - ```Order```와 연관된 ```Product```는 자주 함께 사용되었다. 그래서 ```Order```와 ```Product```는 즉시 로딩으로 설정했다.

  ```java
  @Entity
  public class Member {
    @Id
    @Column(name = "MEMBER_ID")
    private String id;
    private String username;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TEAM_ID",
                nullable = false)
    private Team team;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Order> orders;
  }
  ```

- 위의 엔티티 객체를 보면 ```Team```과의 연관관계를 즉시 로딩으로 설정하였다.  
  이는 회원 엔티티를 조회하면 팀 엔티티도 같이 조회가 된다.


- 회원과 주문내역의 연관관계를 지연 로딩으로 설정하였다.
  따라서 회원 엔티티를 조회하면 연관된 주문내역 엔티티는 프록시로 조회해서 실제 사용 전까지 조회를 지연한다.

<br>

### 컬렉션에 FetchType.EAGER 사용 시 주의점

- 컬렉션을 하나 이상 즉시 로딩하는 것을 권장하지 않는다. 컬렉션과 조인하는 것은 테이블로 보면 일대다 조인이다.  
  일대다의 경우 다쪽에 있는 데이터의 수만큼 결과가 증가한다. 만약 A 테이블을 N,M 테이블과 일대다 조인할 경우 결과가 N * M 이 되면서 너무 많은 데이터를 반환할 수 있고 성능이 저하될 수 있다.


- 컬렉션 즉시 로딩은 항상 외부 조인을 사용한다.
  다대일과 같은 ```{N}ToOne```은 ```not null```제약 조건을 주고 내부 조인을 사용하게 해도 된다.  
  다만 반대로 일대다와 같은 상황에서 내부조인을 하게 되었을 때를 예시로 회원과 팀 관계에서 팀에 소속된 회원이 한명도 없을 경우 팀까지 조회되지 않는 문제가 있어  
  외부 조인을 통해 팀에 대한 모든 데이터를 가져올 수 있도록 설정하는 것이 좋다.

<br><br>

## 영속성 전이 : CASCADE

- 특정 엔티티를 영속 상태로 만들 때 연관된 엔티티도 함께 영속 상태로 만들고자 할때는 영속성 전이를 사용하면 된다.  
  JPA는 ```CASCADE```옵션을 통해 영속성 전의 기능을 제공한다.


- 영속성 전의 기능을 사용하면 부모 엔티티를 저장할 때 자식 엔티티도 함께 저장된다.

<br>

#### 예제 객체

```java
// 부모
@Entity
public class Parent {
    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "parent")
    private List<Child> children = new ArrayList<>();
}

// 자식
@Entity
public class Child {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Parent parent;
}
```

#### 영속상태 만들기

```java
void saveNoCaseCade(){
        // Parent save
        Parent parent=new Parent();
        manager.persist(parent);

        // No.1 child save
        Child child1=new Child();
        child1.setParent(parent); // child.parent setting
        parent.getChildren().add(child1); // parent.child setting
        manager.persist(child1);

        // No.2 child save
        Child child2=new Child();
        child2.setParent(parent); // child.parent setting
        parent.getChildren().add(child2); // parent.child setting
        manager.persist(child2);
        }
```

<br>

### 영속성 전이 : 저장

- 영속성 전이를 활성화하기 위해 ```Parent``` 클래스에서 ```CASCADE```옵션을 적용한다.

```java

@Entity
public class Parent {
    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST)
    private List<Child> children = new ArrayList<>();
}
```

- ```CascadeType.PERSIST``` 옵션은 부모를 영속화할 때 연관된 자식들도 영속시키는 옵션이다.  
  이 옵션을 사용하면 부모와 자식 엔티티를 한 번에 영속화할 수 있다.

  ```java
  @Test
  void saveWithCascade() {
          Child child1 = new Child();
          Child child2 = new Child();
  
          Parent parent = new Parent();
          child1.setParent(parent);
          child2.setParent(parent);
          parent.getChildren().add(child1);
          parent.getChildren().add(child2);
  
          manager.persist(parent);
          transaction.commit();
  }
  ```

<br>

#### 생성 SQL

```jpaql
Hibernate: 
/* insert jpabook.chapter08.Parent
  */ insert 
  into
      parent
      (id) 
  values
      (?)
Hibernate: 
/* insert jpabook.chapter08.Child
  */ insert 
  into
      child
      (parent_id, id) 
  values
      (?, ?)
Hibernate: 
/* insert jpabook.chapter08.Child
  */ insert 
  into
      child
      (parent_id, id) 
  values
      (?, ?)
```

- 코드와 생성된 SQL을 보면 부모 객체인 ```Parent```만 영속상태로 만들었는데 생성된 SQL은 ```Child```까지 저장하고 있다.
- 영속성 전이는 연관관계를 매핑하는 것과는 아무 관련이 없다. 단지 엔티티를 영속화할 때 연관된 엔티티도 같이 영속화하는 편리함을 제공할 뿐이다.


### 영속성 전이 : 삭제
- 영속성 전이는 삭제를 하는 경우에도 사용 가능하다.
- 영속성 전이를 적용하지 않았을 때는 부모 엔티티를 제거하기 위해서는 부모 엔티티를 가지고 있는 자식객체를 모두 변경해야 ```외래키 무결성 오류```가 발생하지 않는다.
- 삭제에 대한 영속성 전이 속성은 ```CascadeType.REMOVE```이다.

<br>

### CASCADE 종류 

```java
public enum CascadeType{
    All, // 모두 적용
    PERSIST, // 영속 
    MERGE, // 병합
    REMOVE, // 삭제
    REFRESH, // REFRESH
    DETACH // DETACH
}
```
- 영속성 전이는 아래와 같이 여러 속성을 함께 쓸 수 있다.
  ```java
  @OneToMany(cascade = {CascadeType.PERSIST,CascadeType.REMOVE})
  ```

- 영속성 전이가 발생하는 시점은 ```Flush```가 될 때이다.

<br><br>

## 고아객체
- JPA는 부모 엔티티와의 관계가 끊어진 자식 엔티티를 자동으로 삭제하는 기능을 제공하며 이를 ```고아객체(ORPHAN)```제거라 한다.  
  이 기능을 사용하여 부모 엔티티의 컬렌셕에서 자식 엔티티의 참조만 제거하면 자식 엔티티가 자동으로 삭제된다.

```java
@Entity
public class Parent {
    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Child> children = new ArrayList<>();
}
```
- 위와 같이 ```Parent``` 객체에 ```orphanRemoval```옵션을 설정해 주도록 하자  
  이제 컬렉션에서 제거한 엔티티는 자동으로 삭제된다.

#### 교재 코드
```java
Parent parent = manager.find(Parent.class, 1L);
parent.getChildren().remove(0);
```
- 내가 이상한건지 교재에 나온 코드로 확인을 하면 ```delete``` sql이 생성되지 않는다.  
  ```commit```이 되어도 ```delete``` sql문을 찾을 수 없었다.


#### 
```java
    @Test
    void test2() {
        Parent parent = new Parent();
        manager.persist(parent);

        Child child1 = new Child();
        child1.setParent(parent);
        parent.getChildren().add(child1);

        Child child2 = new Child();
        child2.setParent(parent);
        parent.getChildren().add(child2);

        manager.flush();
        manager.clear();

        Parent fParent = manager.find(Parent.class, 1L);

        fParent.getChildren().remove(0);
        System.out.println("fParent = " + fParent);
        manager.flush();
        manager.clear();

        Parent fParent2 = manager.find(Parent.class, 1L);
        System.out.println("fParent2 = " + fParent2);
    }
```

- 나는 위의 코드로 했을 때 생성된 SQL에서 ```delete``` 구문을 확인할 수 있었다.  
  위와 같이 작성한 이유는 ```remove()``` 메소드가 SQL이 실행되는 시점은  ```flush```시점이다.   
  그렇기에 ```remove()``` 이후에 ```flush()``` 메소드를 실행하였고 그때서야 ```delete``` SQL을 확인할 수 있었다.

<br>

#### 확인 된 delete sql
```jpaql
Hibernate: 
    /* delete jpabook.chapter08.Child */ delete 
        from
            child 
        where
            id=?
```

- 고아 객체는 참조가 제거된 엔티티는 다른 곳에서 참조하지 않는 고아 객체로 보고 삭제하는 기능이다.  
  따라서 고아 객체를 사용하는 곳은 참조하는 곳이 하나일 경우애만 사용해야 한다. 이러한 문제로 ```orphanRemovel```은 ```@OneToOne```, ```@OneToMany```에서만 사용할 수 있다.

- 고아객체는 개념적으로는 ```CascadeType.REMOVE```와 같다.

<br><br>

### 영속성 전이 + 고아객체, 생명주기
- ```CascadeType.ALL```과 ```orphanRemovel = true```를 동시에 사용하면 부모 엔티티를 통해 자식의 생명 주기를 관리할 수 있다.
  - 예시
    - 자식을 저장하려면 부모에 등록만 하면 된다.
    - 자식을 삭제하려면 부모에서 제거하면 된다.
  - 영속성 전이는 DDD의 Aggregate ROOT 개념을 구현할 때 사용하면 편리하다.

<br><br>

### 정리
- JPA 구현체들은 객체 그래프를 마음껏 탐색할 수 있도록 프록시 기술을 사용하여 지원한다.
- 객체 조회 시 연관된 객체를 주 객체를 조회할 때 같이 조회되도록 하는 것이 ```즉시로딩```, 필요 시 조회하는 것을 ```지연로딩```이라고 한다.
- 부모 엔티티와 연관관계가 끊어진 자식 엔티티를 자동으로 삭제하려면 고아 객체 제거 기능을 사용하면 된다.




