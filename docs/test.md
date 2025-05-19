# Spring Test

---

## @DataJpaTest와 JUnit을 사용한 Repository 테스트

---

### 개요

* Spring Boot 애플리케이션에서 Spring Data JPA를 사용하여 데이터베이스를 다룰 때, JPA Repository가 제대로 동작하는지 테스트하는 것이 중요합니다.

----

### @DataJpaTest

* @DataJpaTest 란? -> JPA Repository 테스트를 위한 애너테이션입니다.
* 최소한의 Spring 컨텍스트만 로드하여 테스트 속도를 높이고, EntityManager와 TestEntityManager를 자동 제공합니다.
~~~
EntityManager란? 

-> JPA(Java Persistence API)에서 제공하는 기본적인 데이터베이스 관리 도구입니다.
   Spring Boot에서 JPA를 사용할 때, 일반적으로 EntityManager를 사용하여 데이터베이스 작업을 수행합니다.

EntityManager의 주요 기능

1. 엔티티 저장: persist(entity)
2. 엔티티 수정: merge(entity)
3. 엔티티 삭제: remove(entity)
4. 엔티티 조회: find(Class<T>, primaryKey), createQuery()

-----------------------------------------------------------------------------------------
EntityTestManager란?

-> 테스트용으로 제공되는 EntityManager입니다.
   Spring Boot의 @DataJpaTest에서 사용할 수 있으며, 일반 EntityManager보다 테스트에 최적화되어 있습니다.
   
TestEntityManager의 특징

1. 테스트 중 사용할 수 있도록 간편한 메서드 제공
2. 트랜잭션이 자동 롤백되어 데이터가 남지 않음
3. flush()를 명시적으로 호출하여 즉시 쿼리를 실행 가능

~~~
* 애플리케이션 전체를 로드하지 않고, JPA 관련 Bean들만 로드하여 테스트 성능을 최적화합니다.

---

### @DataJpaTest의 선택적 파라미터

**properties 속성**
* 테스트 환경에서 특정 Spring Boot 설정 값을 변경할 수 있습니다.
~~~
@DataJpaTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class UserRepositoryTest {
    // 테스트 메서드 작성
}
~~~

**showSql 속성**
* SQL 쿼리 로그를 출력할지 설정하는 옵션입니다.
~~~
@DataJpaTest(showSql = false)
public class UserRepositoryTest {
    // 테스트 메서드 작성
}
~~~

**includeFilters와 excludeFilters**
* 테스트할 컴포넌트(클래스)를 선택적으로 포함하거나 제외할 수 있습니다.

---

### 주요 기능

* 테스트 환경 구성
  * @DataJpaTest는 테스트를 위한 DB 환경을 자동으로 구성해줍니다. 
  * 일반적으로 H2 인메모리 데이터베이스를 사용하여, 테스트 후 자동으로 데이터가 삭제됩니다.

* 의존성 주입
  * @DataJpaTest를 사용하면 Repository 빈이 자동으로 주입되어 테스트할 수 있습니다.

* 기본적으로 트랜잭션 롤백
  * 각 테스트 메서드는 트랜잭션 내부에서 실행되며, 테스트가 끝나면 데이터가 자동으로 롤백됩니다.
  * 테스트 간 데이터가 격리(독립적) 되므로, 테스트 실행 순서에 영향을 받지 않습니다.

---

### Repository 테스트 구현

* 테스트 라이프사이클 관리
~~~
private User testUser;

@BeforeEach
public void setUp() {
    testUser = new User();
    testUser.setUsername("testuser");
    testUser.setPassword("password");
    userRepository.save(testUser);
}

@AfterEach
public void tearDown() {
    userRepository.delete(testUser);
}
~~~
1. @BeforeEach : 각 테스트 시작 전 실행 → 테스트 데이터를 초기화
2. @AfterEach : 각 테스트 종료 후 실행 → 테스트 데이터 정리(삭제)


출처: baeldung



## Service layer Test

---

### 1. UnitTest

* Service layer 만 테스트를 진행한다.
* 외부 의존성은 모두 mocking 해서 처리한다.
* 테스트 속도가 빠르고 로직을 집중적으로 테스트 할 수 있다.

### 2. IntegrationTest(Spring Context 전체를 올림)

* ``@SpringBootTest`` 와 ``@Transactional`` 어노테이션을 사용하여 테스트 진행한다.
* 실제 Bean 들을 다 올리기 때문에 실제 동작을 더 잘 검증할 수 있다.
* 하지만 속도 느리고 무겁다.


## Service Layer Unit 테스트의 문제점

----
* 보통 서비스 계층에서 단위 테스트를 진행하면 mocking 을 통해 진행한다.
* but mocking 은 테스트가 성공한다는 전제를 가지고 있음(행위 기반이면)
* 하지만 해당 로직이 틀린 로직이였다면 ? -> 해당 테스트는 깨진 테스트
* 따라서 해당 문제를 방지하기 위해서 -> 서비스 코드를 작성과 동시에 moking 테스트를 진행하여 올바른 반환값을 확인 + 통합 테스트로 의도하는 대로 동작하는 지 확인하는 방법이 필요
* 결론은 moking 하는 테스트와 통합테스트 둘 다 하는 것이 맞는 것 같다.
