package hello.aop.exam.aop;

import hello.aop.exam.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * Retry 로직 ( request 재 요청시 주로 사용 )
 */
@Slf4j
@Aspect
public class RetryAspect {

//    @Around("@annotation(hello.aop.exam.annotation.Retry")
    @Around("@annotation(retry)") // 이렇게 하면 파람의 타입정보가 @annotation 타입으로 들어가서 깔끔하게 사용 가능하다.
    public Object doRetry(ProceedingJoinPoint joinPoint, Retry retry) throws Throwable {
        log.info("[retry] {} retry={}", joinPoint.getSignature(), retry);

        int maxRetry = retry.value(); // 어노테이션에 설정된 값을 불러온다., retry 할 때는 횟수 제한을 항상 두도록하자
        Exception exceptionHolder = null;

        for (int retryCount = 1; retryCount <= maxRetry; retryCount++) {
            try {
                log.info("[retry] try count={}/{}", retryCount, maxRetry);
                return joinPoint.proceed();
            } catch (Exception e) { // Throwable 을 잡아도 되지만 이 것은 더 상위의 것으로 메모리 풀이 나거나 등등 시스템적이 터지면 그냥 위로 던지게끔 했다.
                exceptionHolder = e; // 발생한 Exception 을 위의 홀더 변수에 넣고 다시 루프가 돈다.
            }
        }
        throw exceptionHolder; // retry 횟수까지 return 못하면 throw 던짐
    }

}
