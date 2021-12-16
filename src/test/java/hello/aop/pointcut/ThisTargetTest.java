package hello.aop.pointcut;

import hello.aop.member.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * JDK 동적 프록시는 인터페이스를 프록시를 만든다 (proxy(this) AOP 대상 x , target(target) AOP 대상 o
 * CGLIB 는 (구현)구체클래스를 상속 받아 프록시를 만든다. (proxy(this) AOP 대상 o, target(target) AOP 대상 o
 * 프록시를 대상으로 하는 this 의 경우 구체 클래스를 지정하면 프록시 생성 전략에 따라서 다른 결과가 나올 수 있다는 점을 알아두자.
 *
 * * 같은패턴을사용할수없다., 부모 타입을 허용한다.
 *
 * 참고
 *  - this , target 지시자는 단독으로 사용되기 보다는 파라미터 바인딩에서 주로 사용된다.
 */
@Slf4j
@Import(ThisTargetTest.ThisTargetAspect.class)
/**
 * 2. 대 스프링부트를 기본으로 쓰면 다 CGLIB 로 만든다.
 * 테스트용으로 application.properties 에 설정할 수 있고(spring.aop.proxy-target-class=false -> JDK Proxy, default ture -> CGLIB)
 * 어노테이션 옵션을 줘도 된다.
 */
//@SpringBootTest
@SpringBootTest(properties = "spring.aop.proxy-target-class=false")
public class ThisTargetTest {

    @Autowired
    MemberService memberService;

    @Test
    public void success() throws Exception {
        log.info("memberService Proxy={}", memberService.getClass());
        memberService.hello("helloA");
    }

    @Slf4j
    @Aspect
    static class ThisTargetAspect {

        @Around("this(hello.aop.member.MemberService)") // 인터페이스
        public Object doThisInterface(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[this-interface] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }

        @Around("target(hello.aop.member.MemberService)") // 인터페이스
        public Object doTargetInterface(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[target-impl] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }

        // 이 경우 동적 프록시 적용 안됨, 설명은 상단 주석 확인
        @Around("this(hello.aop.member.MemberServiceImpl)") // 구체클래스
        public Object doThis(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[this-impl] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }

        @Around("target(hello.aop.member.MemberServiceImpl)") // 구체클래스
        public Object doTarget(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[target-interface] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }
    }
}
