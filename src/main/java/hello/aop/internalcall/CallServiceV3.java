package hello.aop.internalcall;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 구조변경(분리) 권장
 *
 * 내부 호출 자체가 사라지고, callService internalService 를 호출하는 구조로 변경되었다. 덕분에 자연스럽게 AOP 가 적용된다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CallServiceV3 {

    private final InternalService internalService;

    public void external() {
        log.info("call external");
        internalService.internal(); // 외부 메서드 호출
    }
}
