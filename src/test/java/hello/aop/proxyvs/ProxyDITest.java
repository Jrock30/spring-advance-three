package hello.aop.proxyvs;

import hello.aop.member.MemberService;
import hello.aop.member.MemberServiceImpl;
import hello.aop.proxyvs.code.ProxyDIAspect;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * properties = {"spring.aop.proxy-target-class=false"}
 *   - application.properties 에 설정하는 대신에 해당 테스트에서만 설정을 임시로 적용한다. 이렇게 하면 각 테스트마다 다른 설정을 손쉽게 적용할 수 있다.
 * spring.aop.proxy-target-class=false : 스프링이 AOP 프록시를 생성할 때 JDK 동적 프록시를 우선 생성한다.
 * 물론 인터페이스가 없다면 CGLIB 를 사용한다.
 */
@Slf4j
//@SpringBootTest(properties = {"spring.aop.proxy-target-class=false"}) // 기본으로 CGLIB 씀으로써 설정해줌, JDK 동적 프록시
//@SpringBootTest(properties = {"spring.aop.proxy-target-class=true"}) // 기본으로 CGLIB 씀으로써 설정해줌, CGLIB
@SpringBootTest
@Import(ProxyDIAspect.class)
public class ProxyDITest {

    @Autowired
    MemberService memberService;

    /**
     * 타입과 관련된 예외가 발생한다.
     * 자세히 읽어보면 memberServiceImpl 에 주입되길 기대하는 타입은 hello.aop.member.MemberServiceImpl 이지만
     * 실제 넘어온 타입은 com.sun.proxy.$Proxy54 이다. 따라서 타입 예외가 발생한다고 한다.
     *
     * JDK Interface TRUE
     *   @Autowired MemberService memberService
     *     - 이 부분은 문제가 없다. JDK Proxy는 MemberService 인터페이스를 기반으로 만들어진다. 따라서 해당 타입으로 캐스팅 할 수 있다.
     *     - MemberService = JDK Proxy 가 성립한다.
     *   @Autowired MemberServiceImpl memberServiceImpl
     *     - 문제는 여기다. JDK Proxy는
     *     - MemberService 인터페이스를 기반으로 만들어진다. 따라서 MemberServiceImpl 타입이 뭔지 전혀 모른다. 그래서 해당 타입에 주입할 수 없다.
     *     - MemberServiceImpl = JDK Proxy 가 성립하지 않는다.
     *
     * CGLIB - TRUE
     *   @Autowired MemberService memberService
     *     - CGLIB Proxy는 MemberServiceImpl 구체 클래스를 기반으로 만들어진다.
     *     - MemberServiceImpl 은 MemberService 인터페이스를 구현했기 때문에 해당 타입으로 캐스팅 할 수 있다.
     *     - MemberService = CGLIB Proxy 가 성립한다.
     *   @Autowired MemberServiceImpl memberServiceImpl
     *     - CGLIB Proxy는 MemberServiceImpl
     *     - 구체 클래스를 기반으로 만들어진다. 따라서 해당 타입으로 캐스팅 할 수 있다. MemberServiceImpl = CGLIB Proxy 가 성립한다.
     */
    @Autowired
    MemberServiceImpl memberServiceImpl; // JDK 동적 프록시는 Exception 발생

    @Test
    public void go() throws Exception {
        log.info("memberService class={}", memberService.getClass());
        log.info("memberServiceImpl class={}", memberServiceImpl.getClass());
        memberServiceImpl.hello("hello");
    }
}
