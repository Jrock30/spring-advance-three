package hello.aop.internalcall;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 프록시와 내부 호출
 *
 * external() 을 호출하면 안에서 internal()을 호출하게 되는데
 * external() 은 프록시로 만들어지나 internal 은 this.internal() 즉 해당 타켓의 메서드를 실행하는 거라 aop 가 작동하지 않음.
 * V1, V2 로 해결
 */
@Slf4j
@Component
public class CallServiceV0 {

    public void external() {
        log.info("call external");
        internal(); // 내부 메서드 호출(this.internal())
    }

    public void internal() {
        log.info("call internal");
    }
}
