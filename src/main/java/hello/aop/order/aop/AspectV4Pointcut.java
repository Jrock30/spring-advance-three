package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Slf4j
@Aspect // 애스펙트라는 표식이지 컴포넌트 스캔이 되는 것은 아니다. 따라서 AspectV1 를 AOP 로 사용하려면 스프링 빈으로 등록해야 한다.
public class AspectV3 {

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
     * allOrder() 포인트컷은 hello.aop.order 패키지와 하위 패키지를 대상으로 한다.
     * allService() 포인트컷은 타입 이름 패턴이 *Service 를 대상으로 하는데 쉽게 이야기해서 XxxService 처럼 Service 로 끝나는 것을 대상으로 한다.
     * *Servi* 과 같은 패턴도 가능하다. 여기서 타입 이름 패턴이라고 한 이유는 클래스, 인터페이스에 모두 적용되기 때문이다.
     * @Around("allOrder() && allService()") 포인트컷은 이렇게 조합할 수 있다. && (AND), || (OR), ! (NOT) 3가지 조합이 가능하다.
     * hello.aop.order 패키지와 하위 패키지 이면서 타입 이름 패턴이 *Service 인 것을 대상으로 한다.
     * 결과적으로 doTransaction() 어드바이스는 OrderService 에만 적용된다.
     * doLog() 어드바이스는 OrderService , OrderRepository 에 모두 적용된다.
     *
     * orderService : doLog() , doTransaction() 어드바이스 적용
     * orderRepository : doLog() 어드바이스 적용
     *
     */
    // 클래스 이름 패턴이 *Service ( 보통의 트랜잭션 이라고 생각하고 구현 )
    @Pointcut("execution(* *..*Service.*(..))")
    private void allService(){}


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

    // hello.aop.order 패키지와 하위 패키지 이면서 클래스 이름 패턴이 *Service
    @Around("allOrder() && allService()")
    public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable {

        try {
            log.info("[트랜잭션 시작] {}", joinPoint.getSignature());
            Object result = joinPoint.proceed();
            log.info("[트랜잭션 커밋] {}", joinPoint.getSignature());
            return result;
        } catch (Exception e) {
            log.info("[트랜잭션 롤백] {}", joinPoint.getSignature());
            throw e;
        } finally {
            log.info("[리소스 릴리즈] {}", joinPoint.getSignature());
        }
    }

}
