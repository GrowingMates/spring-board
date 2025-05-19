package com.support;

import org.springframework.test.annotation.DirtiesContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public @interface IsolatedTest { // 스프링 컨텍스트 재로딩
    // 특정 케이스에만 사용
    // - DB 트랜잭션으로 롤백되지 않는 외부 시스템과 연동되는 경우
    // - 스프링 컨텍스트 초기화 필요시
    // - 정적(static) 변수값이 공유되는데 초기화 필요시
    // - 비동기 처리(@Async, @Scheduled 등) 테스트 시
}
