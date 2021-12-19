package hello.aop.internalcall;

import hello.aop.internalcall.aop.CallLogAspect;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Import(CallLogAspect.class)
@SpringBootTest // 스프링 컨테이너 띄워줌
class CallServiceV0Test {

    @Autowired
    private CallServiceV0 callServiceV0; // 프록시에서 잡음, 즉 프록시로 생성

    @Test
    void external() {
        log.info("target={}", callServiceV0.getClass());
        /**
         * external() 은 프록시가 적용되나 (AOP 걸림)
         * external() 안에서 internal() 을 호출 되면 어드바이스가 호출되지 않음. (AOP 안걸림)
         *
         * 따로 아래의 internal() 을 호출하면 프록시 적용 됨.(AOP 걸림)
         *
         * 자바 언어에서 메서드 앞에 별도의 참조가 없으면 this 라는 뜻으로 자기 자신의 인스턴스를 가리킨다.
         * 결과적으로 자기 자신의 내부 메서드를 호출하는 this.internal() 이 되는데, 여기서 this 는 실제 대상 객체(target)의 인스턴스를 뜻한다.
         * 결과적으로 이러한 내부 호출은 프록시를 거치지 않는다. 따라서 어드바이스도 적용할 수 없다.
         *
         */
        callServiceV0.external();
    }

    @Test
    void internal() {
        callServiceV0.internal();
    }
}