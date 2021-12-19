package hello.aop.internalcall;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 자기자신 주입
 *
 * 생성자 주입은 순환 사이클을 만들기 때문에 실패한다. (자기자신이 생성도 안됬는데 주입을 어떻게 하겠는가)
 *
 * callServiceV1 를 수정자를 통해서 주입 받는 것을 확인할 수 있다.
 * 스프링에서 AOP 가 적용된 대상을 의존관계 주입 받으면 주입 받은 대상은 실제 자신이 아니라 프록시 객체이다.
 * external() 을 호출하면 callServiceV1.internal() 를 호출하게 된다.
 * 주입받은 callServiceV1 은 프록시이다. 따라서 프록시를 통해서 AOP 를 적용할 수 있다
 *
 * 스프링 부트 2.6 부터는 순환 참조를 기본으로 금지 함. 아래의 옵션을 추가 해주어야 함.
 *   - spring.main.allow-circular-references=true
 *
 */
@Slf4j
@Component
public class CallServiceV1 {

    private CallServiceV1 callServiceV1;

    @Autowired
    private void setCallServiceV1(CallServiceV1 callServiceV1) {
        log.info("callServiceV1 setter={}", callServiceV1.getClass()); // 프록시 생성 확인
        this.callServiceV1 = callServiceV1;
    }

    public void external() {
        log.info("call external");
        callServiceV1.internal(); // 자기자신 주입 된 것을 호출 (외부 메서드 호출)
    }

    public void internal() {
        log.info("call internal");
    }
}
