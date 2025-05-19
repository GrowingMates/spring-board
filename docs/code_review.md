# /api 는 클래스 레벨보다는 yml로 따로 빼는 건 어떨까요?

---
## 문제점

* 개발할 때는 /api 경로로 접근하지만 배포 환경에서는 /prod 경로로 접근한다고 가정한다. -> @RequestMapping("/api")를 전부 /prod로 바꿔야 한다.
* 또한 URI.create(~~) 부분도 모두 수정해야 한다.

## 해결

* application.yml 파일에 경로를 미리 설정한다.
* URI 부분은 ServletUriComponentsBuilder의 fromCurrentRequestUri() 메서드를 사용하여 해결한다. -> 해당 메서드는 현재 요청을 보내는 경로를 받아오는 기능을 제공한다. 

# 에러 코드 재사용성

---
## 문제점(현재 에러 코드 구조)

~~~
public class GlobalExceptionHandler {
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<GlobalErrorResponse> handleException(GlobalException e) {
    }
}

@Getter
public enum GlobalErrorCode {

}

@Getter
public class GlobalException extends RuntimeException {

    private final GlobalErrorCode errorCode;

    public GlobalException(GlobalErrorCode errorCode) {
    }
}
~~~

* 현재 이 3단계 구조로, 특정 관심사 패키지마다 exception 을 관리하고 있음
* 이렇게 할 경우, 패키지마다 계속해서 exception 을 생성 및 관리해야함 -> 코드 재활용이 안됌.

## 해결책

* common 패키지에, 공통적으로 사용되는 errorCode 를 interface화 시킴.
* 이후 interface(errorCode) 를 사용한 BaseException 클래스를 생성 -> 다른 패키지 exception 에서 BaseException 상속받아 사용할 수 있음
* 그렇기에 exception handler 도 하나로만 관리 가능

## 결론

* ErrorCode 인터페이스 하나 파서 관리
* BaseException 생성, 그리고 이 클래스를 이용한  BaseExceptionHandler 생성 -> 핸들러 하나만 사용 가능
* MemberException, ArticleException 등등 BaseException 을 상속받아 BaseExceptionHandler 에서 에러 처리 가능
* ErrorCode 추가될때마다 exception 을 추가할 필요가 없어짐.

---

# Offset Paging

--- 
## 장점
* 구현이 쉽다 -> JPA에서 바로 Pageable 객체로 페이징 가능
* 페이지 번호가 있는 프론트 구현 시, 잘 맞는다.

## 단점
* OFFSET 100000 같이 뒤 페이지로 갈수록 느려짐 → 인덱스가 있어도 느림 // 해당 문제는 커서 기반 페이징을 통해 해결 가능 -> offset 은 처음부터 모든 column을 조회해서 속도가 느리지만 커서 기반 페이징은 특정 id 를 기준으로 해당 부분부터 조회하기에 빠르다
* 페이징 도중 데이터가 추가/삭제되면 순서가 뒤틀릴 수 있음 (같은 댓글이 여러 번 보이거나 사라짐)


# ```@ManyToOne``` vs 간접참조

---

## @ManyToOne

* 객체를 통째로 다루기 떄문에 저장이 간편하다.
* 그렇기에 객체에 직접적으로 바로 접근이 가능하다.
* 하지만 연관관계 이슈 때문에 객체 조회 시 N+1 문제가 발생할 수 있다.
* 따라서 관계에 따라 접근 전략을 모색해야 한다.

## 간접참조

* id 값만 저장하면 된다.(단순하고 가볍게 관리 가능)
* 따라서, id 값을 통해 조회를 또 다시 해야한다.
* 하지만 이는 객체지향과는 조금 거리가 멀다.(객체가 아닌, 단순 id 값만 가지고 있기 때문이다.)

# ORM의 탄생 배경

---

* ORM 은 DB를 테이블처럼 다루지 말고 객체 처럼 다루자는 목표로 등장.


# 객체 관계에 따른 차이

---

## 단방향 매핑 (OneToMany or ManyToOne 한쪽만)

* Comment → Article만 있는 구조 or Article -> Comment 만 있는 구조
* 조회나 저장할 때 한쪽에서만 조작 가능
~~~
@Entity
public class Comment {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;
}

댓글을 저장할 때 어떤 게시글에 달리는지는 필요함 → Comment → Article
하지만 Article 입장에서는 굳이 댓글들을 알고 있을 필요 없음
~~~

## 양방향 매핑
* Article → Comment, Comment → Article 모두 존재
* 양쪽이 서로를 참조하는 구조
~~~
// Article
@OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
private List<Comment> comments;

// Comment
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "article_id")
private Article article;

게시글 상세 페이지에서 댓글도 같이 조회하고 싶다 or 게시글 삭제 시 댓글도 자동 삭제되게 하고 싶다
~~~

