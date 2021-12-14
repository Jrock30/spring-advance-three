package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Slf4j
@Aspect // 애스펙트라는 표식이지 컴포넌트 스캔이 되는 것은 아니다. 따라서 AspectV1 를 AOP 로 사용하려면 스프링 빈으로 등록해야 한다.
public class AspectV2 {

    /**
     *  - @Pointcut 에 포인트컷 표현식을 사용한다.
     *  - 메서드 이름과 파라미터를 합쳐서 포인트컷 시그니처(signature)라 한다.
     *  - 메서드의 반환 타입은 void 여야 한다.
     *  - 코드 내용은 비워둔다.
     *  - 포인트컷 시그니처는 allOrder() 이다. 이름 그대로 주문과 관련된 모든 기능을 대상으로 하는 포인트컷이다.
     *  - @Around 어드바이스에서는 포인트컷을 직접 지정해도 되지만, 포인트컷 시그니처를 사용해도 된다. 여기서는 @Around("allOrder()") 를 사용한다.
     *  - private , public 같은 접근 제어자는 내부에서만 사용하면 private 을 사용해도 되지만, 다른 애스팩트에서 참고하려면 public 을 사용해야 한다.
     */
    // hello.aop.order 패키지와 하위 패키지
    @Pointcut("execution(* hello.aop.order..*(..))")
    private void allOrder(){} // pointcut signature

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
     */
    @Around("allOrder()")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[log] {}", joinPoint.getSignature()); // join point signature
        return joinPoint.proceed();
    }

}
