package hello.aop.internalcall;

import hello.aop.internalcall.aop.CallLogAspect;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * 자기자신 주입, V0 보완 프록시 적용
 */
@Slf4j
@Import(CallLogAspect.class)
@SpringBootTest // 스프링 컨테이너 띄워줌
class CallServiceV1Test {

    @Autowired
    private CallServiceV1 callServiceV1;

    @Test
    void external() {
        log.info("target={}", callServiceV1.getClass());
        callServiceV1.external();
    }
}