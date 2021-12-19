package hello.aop.internalcall;

import hello.aop.internalcall.aop.CallLogAspect;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * 지연 조회
 */
@Slf4j
@Import(CallLogAspect.class)
@SpringBootTest // 스프링 컨테이너 띄워줌
class CallServiceV2Test {

    @Autowired
    private CallServiceV2 callServiceV2;

    @Test
    void external() {
        log.info("target={}", callServiceV2.getClass());
        callServiceV2.external();
    }
}