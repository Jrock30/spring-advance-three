package hello.aop.internalcall.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Slf4j
@Aspect
public class CallLogAspect {

    // 접근제어자?(생략가능), hello.aop.internalcall..*(해당 패키지 하위패키지 모두), .* (메서드 모두), (..) (숫자와 무관하게 모든 파라미터, 모든 타입 허용)
    @Before("execution(* hello.aop.internalcall..*.*(..))")
    public void doLog(JoinPoint joinPoint) {
        log.info("aop={}", joinPoint.getSignature());

    }
}
