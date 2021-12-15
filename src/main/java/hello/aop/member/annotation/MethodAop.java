package hello.aop.member.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // 메소드에 적용되는 어노테이션
@Retention(RetentionPolicy.RUNTIME) // 실행시점에 읽음
public @interface MethodAop {
    String value();
}
