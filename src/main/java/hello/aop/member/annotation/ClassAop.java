package hello.aop.member.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE) // 클래스에 붙이는 어노테이션
@Retention(RetentionPolicy.RUNTIME) // 런타임 환경에 구동
public @interface ClassAop {
}
