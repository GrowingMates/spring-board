package com.config.auth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER) // 메서드의 파라미터에만 적용
@Retention(RetentionPolicy.RUNTIME) // 실행 시점까지 유지
public @interface AuthenticatedMember {
}
