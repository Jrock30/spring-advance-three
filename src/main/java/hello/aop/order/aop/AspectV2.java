package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Slf4j
@Aspect // 애스펙트라는 표식이지 컴포넌트 스캔이 되는 것은 아니다. 따라서 AspectV1 를 AOP 로 사용하려면 스프링 빈으로 등록해야 한다.
public class AspectV1 {

    /**
     *  - @Around 애노테이션의 값인 execution(* hello.aop.order..*(..)) 는 포인트컷이 된다.
     *  - @Around 애노테이션의 메서드인 doLog 는 어드바이스( Advice )가 된다.
     *  - execution(* hello.aop.order..*(..)) 는 hello.aop.order 패키지와 그 하위 패키지( .. )를 지정하는 AspectJ 포인트컷 표현식이다.
     *  - 이제 OrderService , OrderRepository 의 모든 메서드는 AOP 적용의 대상이 된다.
     *    참고로 스프링은 프록시 방식의 AOP 를 사용하므로 프록시를 통하는 메서드만 적용 대상이 된다.
     *
     *  - 스프링 AOP 는 AspectJ의 문법을 차용하고, 프록시 방식의 AOP 를 제공한다. AspectJ를 직접 사용하는 것이 아니다.
     *    스프링 AOP 를 사용할 때는 @Aspect 애노테이션을 주로 사용하는데, 이 애노테이션도 AspectJ가 제공하는 애노테이션이다.
     *
     *
     *
     */
    @Around("execution(* hello.aop.order..*(..))")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[log] {}", joinPoint.getSignature()); // join point signature
        return joinPoint.proceed();
    }

}
