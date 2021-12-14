package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

/**
 * Advice 종류
 *  - @Around 를 제외한 나머지 어드바이스들은 @Around 가 할 수 있는 일의 일부만 제공할 뿐이다.
 *    따라서 @Around 어드바이스만 사용해도 필요한 기능을 모두 수행할 수 있다.
 *  - 모든 어드바이스는 org.aspectj.lang.JoinPoint 를 첫번째 파라미터에 사용할 수 있다. (생략해도 된다.)
 *    단 @Around 는 ProceedingJoinPoint 을 사용해야 한다.
 *    참고로 ProceedingJoinPoint 는 org.aspectj.lang.JoinPoint 의 하위 타입이다.
 *
 *  - ProceedingJoinPoint 인터페이스의 주요 기능 proceed()
 *    - 다음 어드바이스나 타켓을 호출한다
 *
 *  - JoinPoint 인터페이스 주요 기능
 *    - getArgs() : 메서드 인수를 반환합니다.
 *    - getThis() : 프록시 객체를 반환합니다.
 *    - getTarget() : 대상 객체를 반환합니다.
 *    - getSignature() : 조언되는 메서드에 대한 설명을 반환합니다. toString() : 조언되는 방법에 대한 유용한 설명을 인쇄합니다.
 *
 *  - 스프링은 5.2.7 버전부터 동일한 @Aspect 안에서 동일한 조인포인트의 우선순위를 정했다.
 *    - 실행 순서: @Around , @Before , @After , @AfterReturning , @AfterThrowing
 *    - 어드바이스가 적용되는 순서는 이렇게 적용되지만, 호출 순서와 리턴 순서는 반대라는 점을 알아두자.
 *    - 물론 @Aspect 안에 동일한 종류의 어드바이스가 2개 있으면 순서가 보장되지 않는다. 이 경우 @Aspect 를 분리하고 @Order 를 적용하자.
 *
 *
 */
@Slf4j
@Aspect
public class AspectV6Advice {

    /**
     * @Around 는 ProceedingJoinPoint.proceed() 를 호출해야 다음 대상이 호출된다
     * 만약 호출하지 않으면 다음 대상이 호출되지 않는다.
     *
     *  - 메서드의 실행의 주변에서 실행된다. 메서드 실행 전후에 작업을 수행한다.
     *  - 가장 강력한 어드바이스
     *    - 조인 포인트 실행 여부 선택 joinPoint.proceed() 호출 여부 선택
     *    - 전달 값 변환: joinPoint.proceed(args[])
     *    - 반환 값 변환
     *    - 예외 변환
     *    - 트랜잭션 처럼 try ~ catch~ finally 모두 들어가는 구문 처리 가능
     *  - 어드바이스의 첫 번째 파라미터는 ProceedingJoinPoint 를 사용해야 한다.
     *    - proceed() 를 통해 대상을 실행한다.
     *    - proceed() 를 여러번 실행할 수도 있음(재시도)
     */
    @Around("hello.aop.order.aop.PointCuts.orderAndService()")
    public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable {

        try {
            // @Before
            log.info("[트랜잭션 시작] {}", joinPoint.getSignature());
            Object result = joinPoint.proceed();
            // @AfterReturning
            log.info("[트랜잭션 커밋] {}", joinPoint.getSignature());
            return result;
        } catch (Exception e) {
            // @AfterThrowing
            log.info("[트랜잭션 롤백] {}", joinPoint.getSignature());
            throw e;
        } finally {
            // @After
            log.info("[리소스 릴리즈] {}", joinPoint.getSignature());
        }
    }

    /**
     * @Before - 조인 포인트(target) 실행 이전에 실행
     * joinPoint.proceed() 을 실행할 필요 없다. joinPoint.proceed() 전 단계 훅이라고 생각하자.
     */
    @Before("hello.aop.order.aop.PointCuts.orderAndService()")
    public void doBefore(JoinPoint joinPoint) {
        log.info("[before] {}", joinPoint.getSignature());
    }

    /**
     * @AfterReturning - 조인 포인트(target)가 정상 완료후 실행
     * returning = "result" -> parameter result , 이름이 같아야 한다.
     *
     * returning 절에 지정된 타입의 값을 반환하는 메서드만 대상으로 실행한다. (부모 타입을 지정하면 모든자식 타입은 인정된다.)
     * @Around 와 다르게 반환되는 객체를 변경할 수는 없다.
     * 반환 객체를 변경하려면 @Around 를 사용해야 한다. 참고로 반환 객체를 조작할 수 는 있다.
     */
    @AfterReturning(value = "hello.aop.order.aop.PointCuts.orderAndService()", returning = "result")
    public void doReturn(JoinPoint joinPoint, Object result) {
        log.info("[return] {} return={}", joinPoint.getSignature(), result);
    }

    /**
     * @AfterReturning - 메서드가 예외를 던지는 경우 실행
     * throwing = "ex" -> parameter ex , 이름이 같아야 한다.
     * throwing 절에 지정된 타입과 맞은 예외를 대상으로 실행한다. (부모 타입을 지정하면 모든 자식 타입은 인정된다.)
     */
    @AfterThrowing(value = "hello.aop.order.aop.PointCuts.orderAndService()", throwing = "ex")
    public void doThrowing(JoinPoint joinPoint, Exception ex) {
        log.info("[ex] {} message={}", joinPoint.getSignature(), ex);
    }

    /**
     * @After - 조인 포인트(target)가 정상 또는 예외에 관계없이 실행(finally)
     * joinPoint.proceed() 을 실행할 필요 없다. joinPoint.proceed() 후 단계 훅이라고 생각하자.
     *
     * 일반적으로 리소스를 해제하는 데 사용한다.
     */
    @After(value = "hello.aop.order.aop.PointCuts.orderAndService()")
    public void doAfter(JoinPoint joinPoint) {
        log.info("[after] {}", joinPoint.getSignature());
    }
}
