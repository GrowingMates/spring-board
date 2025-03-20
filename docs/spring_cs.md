## @Transactional(readOnly = true) 를 사용한 경우 vs 사용하지 않은 경우

---

### 1. @Transactional(readOnly = true)를 사용하지 않은 경우

1. 트랜잭션이 시작됩니다.
2. SELECT 쿼리를 실행하여 데이터 조회합니다.
3. 트랜잭션이 쓰기 모드이므로 변경 감지(Dirty Checking)가 활성화됩니다.
4. 조회한 엔티티를 영속성 컨텍스트(Persistence Context)에 저장합니다.
5. JPA가 Book 엔티티를 관리하기 시작합니다.
6. 트랜잭션이 종료되면, 변경된 엔티티가 있으면 UPDATE 쿼리를 실행합니다.
7. 변경이 없어도 flush()가 호출되면서 DB와 동기화 수행합니다. → 불필요한 성능 저하 가능


### 2. @Transactional(readOnly = true) 사용한 경우

1. 트랜잭션이 시작됩니다. (READ ONLY)
2. SELECT 쿼리를 실행하여 데이터 조회합니다.
3. 쓰기 관련 기능(변경 감지, flush, dirty checking)이 비활성화 됩니다.
4. JPA가 엔티티를 관리하지 않습니다. (영속성 컨텍스트 제외)
5. 트랜잭션이 종료됩니다.

