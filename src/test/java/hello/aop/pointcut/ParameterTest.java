package hello.aop.pointcut;

import hello.aop.member.MemberService;
import hello.aop.member.annotation.ClassAop;
import hello.aop.member.annotation.MethodAop;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * 다음은 포인트컷 표현식을 사용해서 어드바이스에 매개변수를 전달할 수 있다.
 * this, target, args,@target, @within, @annotation, @args
 *
 * 포인트컷의 이름과 매개변수의 이름을 맞추어야 한다.
 */
@Slf4j
@Import(ParameterTest.ParameterAspect.class)
@SpringBootTest
public class ParameterTest {

    @Autowired
    MemberService memberService;

    @Test
    public void success() throws Exception {
        log.info("memberService proxy={}", memberService.getClass());
        memberService.hello("helloA");
    }

    @Slf4j
    @Aspect
    static class ParameterAspect {

        @Pointcut("execution(* hello.aop.member..*.*(..))")
        private void allMember() { // 이 메서드로 포인트컷 사용 가능
        }

        /**
         * 파람을 ProceedingJoinPoint joinPoint.getArgs() 로 꺼내는 방법
         */
        @Around("allMember()")
        public Object logArgs1(ProceedingJoinPoint joinPoint) throws Throwable {
            Object arg1 = joinPoint.getArgs()[0]; // 타겟의 리턴 값이 넘어온다. ("helloA")
            log.info("[logArgs1]{}, arg={}", joinPoint.getSignature(), arg1);
            return joinPoint.proceed();
        }

        /**
         * 파람을 pointcut 을 이용해서 받는 방법
         * - 포인트컷의 이름과 매개변수의 이름을 맞추어야 한다. args(arg) -> Obejct arg
         */
        @Around("allMember() && args(arg, ..)")
        public Object logArgs2(ProceedingJoinPoint joinPoint, Object arg) throws Throwable {
            // 타겟의 리턴 값이 넘어온다. ("helloA")
            log.info("[logArgs2]{}, arg={}", joinPoint.getSignature(), arg);
            return joinPoint.proceed();
        }

        /**
         * @Before 를 사용해서 파람을 꺼내는 방법
         */
        @Before("allMember() && args(arg, ..)")
        public void logArgs3(String arg) {
            log.info("[logArgs3] arg={}", arg);
        }

        /**
         * @Before this 를 활용해서 꺼내는 방법
         *  - JoinPoint 파람은 써도 되고 안써도 된다.
         *  - 타입을 직접 지정
         *  - 프록시 객체가 리턴 됨 (MemberServiceImpl$$EnhancerBySpringCGLIB$$9f2b9cae)
         *  - 스프링 빈 객체(스프링 AOP 프록시)를 대상으로 하는 조인 포인트
         *  - * 같은패턴을사용할수없다., 부모 타입을 허용한다.
         *
         *  JDK 동적 프록시는 인터페이스를 프록시를 만든다 (proxy(this) AOP 대상 x , target(target) AOP 대상 o
         *  CGLIB 는 (구현)구체클래스를 상속 받아 프록시를 만든다. (proxy(this) AOP 대상 o, target(target) AOP 대상 o
         *  프록시를 대상으로 하는 this 의 경우 구체 클래스를 지정하면 프록시 생성 전략에 따라서 다른 결과가 나올 수 있다는 점을 알아두자.
         */
        @Before("allMember() && this(obj)")
        public void thisArgs(JoinPoint joinPoint, MemberService obj) {
            log.info("[this]{}, obj={}", joinPoint.getSignature(), obj.getClass());
        }

        /**
         * @Before target 을 활용해서 꺼내는 방법
         *  - JoinPoint 파람은 써도 되고 안써도 된다.
         *  - 타입을 직접 지정
         *  - 실제 대상 구현체가 리턴 됨 (MemberServiceImpl)
         *  - Target 객체(스프링 AOP 프록시가 가르키는 실제 대상)를 대상으로 하는 조인 포인트
         *  - * 같은패턴을사용할수없다., 부모 타입을 허용한다.
         *
         *  JDK 동적 프록시는 인터페이스를 프록시를 만든다 (proxy(this) AOP 대상 x , target(target) AOP 대상 o
         *  CGLIB 는 (구현)구체클래스를 상속 받아 프록시를 만든다. (proxy(this) AOP 대상 o, target(target) AOP 대상 o
         *  프록시를 대상으로 하는 this 의 경우 구체 클래스를 지정하면 프록시 생성 전략에 따라서 다른 결과가 나올 수 있다는 점을 알아두자.
         */
        @Before("allMember() && target(obj)")
        public void targetArgs(JoinPoint joinPoint, MemberService obj) {
            log.info("[target]{}, obj={}", joinPoint.getSignature(), obj.getClass());
        }

        /**
         * @Before @target 을 활용해서 꺼내는 방법
         *  - JoinPoint 파람은 써도 되고 안써도 된다.
         *  - 클래스에 적용된 어노테이션 정보를 가져온다. (ClassAop())
         *  - 부모까지 타입 허용
         */
        @Before("allMember() && @target(annotation)")
        public void atTarget(JoinPoint joinPoint, ClassAop annotation) {
            log.info("[@target]{}, obj={}", joinPoint.getSignature(), annotation);
        }

        /**
         * @Before @within 을 활용해서 꺼내는 방법
         *  - JoinPoint 파람은 써도 되고 안써도 된다.
         *  - 클래스에 적용된 어노테이션 정보를 가져온다. (ClassAop())
         *  - 자기자신만 가능
         */
        @Before("allMember() && @within(annotation)")
        public void atWithin(JoinPoint joinPoint, ClassAop annotation) {
            log.info("[@within]{}, obj={}", joinPoint.getSignature(), annotation);
        }

        /**
         * @Before @annotation 을 활용해서 꺼내는 방법
         *  - JoinPoint 파람은 써도 되고 안써도 된다.
         *  - 메서드에 적용된 어노테이션 정보를 가져온다. (MethodAop())
         *  - 어노테이션에 적용 된 값을 가져올 수 있다.
         */
        @Before("allMember() && @annotation(annotation)")
        public void atAnnotation(JoinPoint joinPoint, MethodAop annotation) {
            log.info("[@annotation]{}, annotationValue={}", joinPoint.getSignature(), annotation.value());
        }
    }
}
