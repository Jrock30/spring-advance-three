package hello.aop.pointcut;

import hello.aop.member.MemberService;
import hello.aop.member.MemberServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.*;

@Slf4j
public class ExecutionTest {
    // 포인트 컷 표현식 처리해 주는 클래스
    AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
    Method helloMethod;

    @BeforeEach
    public void init() throws NoSuchMethodException {
        helloMethod = MemberService.class.getMethod("hello", String.class);
    }

    @Test
    public void printMethod() throws Exception {
        // helloMethod=public abstract java.lang.String hello.aop.member.MemberService.hello(java.lang.String)
        log.info("helloMethod={}", helloMethod);
    }

    @Test
    public void exactMatch() throws Exception {
        /**
         * helloMethod=public abstract java.lang.String hello.aop.member.MemberService.hello(java.lang.String)
         * 접근제어자? 반환타입 선업타입?메서드이름(파람) 예외?
         *
         * 정확하게 모두 일치되는 조건
         */
        pointcut.setExpression("execution(public String hello.aop.member.MemberServiceImpl.hello(String))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    public void allMatch() throws Exception {
        /**
         *
         * 접근제어자?: 생략
         * 반환타입: *
         * 선언타입?: 생략
         * 메서드이름: * 파라미터: (..)
         *
         * 가장 많이 생략한 조건
         */
        pointcut.setExpression("execution(* *(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    public void nameMatch() throws Exception {
        /**
         * 이름이 일치하는 조건
         */
        pointcut.setExpression("execution(* hello(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    public void nameMatchStar1() throws Exception {
        /**
         * 이름이 일치하는 조건 (패턴 패칭1)
         */
        pointcut.setExpression("execution(* hel*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    public void nameMatchStar2() throws Exception {
        /**
         * 이름이 일치하는 조건 (패턴 패칭2)
         */
        pointcut.setExpression("execution(* *el*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    public void nameMatchFalse() throws Exception {
        /**
         * 이름이 일치하지 않는 조건
         */
        pointcut.setExpression("execution(* nono(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    @Test
    public void packageExactMatch1() throws Exception {
        /**
         * 패키지가 일치하는 조건
         */
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.hello(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    public void packageExactMatch2() throws Exception {
        /**
         * 패키지가 일치하는 조건 (타입, 메서드에 패턴 적용)
         */
        pointcut.setExpression("execution(* hello.aop.member.*.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    public void packageExactMatchFalse() throws Exception {
        /**
         * 패키지가 일치하지 않는 조건 (타입, 메서드에 패턴 적용)
         *
         * . : 정확하게 해당 위치의 패키지
         * .. : 해당 위치의 패키지와 그 하위 패키지도 포함
         * 주의사항 - 아래와 같은 경우는 aop 패키지 내에서만 찾고 한 단계 더 들어간 패키지는 찾지 않아서 실패한다
         */
        pointcut.setExpression("execution(* hello.aop.*.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    @Test
    public void packageExactMatchSubPackage() throws Exception {
        /**
         * 패키지가 일치하지 하는 조건 (타입, 메서드에 패턴 적용)
         *
         * . : 정확하게 해당 위치의 패키지
         * .. : 해당 위치의 패키지와 그 하위 패키지도 포함
         * 주의사항 - 아래와 같은 경우는 aop..* 로 들어가면 서브패키지(하위패키지) 까지 포함한다.
         */
        pointcut.setExpression("execution(* hello.aop..*.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    public void typeExactMatch() throws Exception {
        /**
         *  Type 이 일치하는 조건
         */
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    public void typeMatchSuperType() throws Exception {
        /**
         *  Type 이 일치하는 조건 ( Interface )
         *  부모타입 또한 매칭이 된다.
         *  execution 에서는 MemberService 처럼 부모 타입을 선언해도 그 자식 타입은 매칭된다.
         *  다형성에서 부모타입 = 자식타입 이 할당 가능하다는 점을 떠올려보면 된다.
         */
        pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    public void typeMatchInternal() throws Exception {
        /**
         *  Type 이 일치하는 조건 ( Interface )
         *  부모 타입에 없는 자식 메서드는 일치하지 않는다.
         *  부모에는(interface) internal() 메서드가 없다.
         */
        pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");

        Method internalMethod = MemberServiceImpl.class.getMethod("internal", String.class);
        assertThat(pointcut.matches(internalMethod, MemberServiceImpl.class)).isFalse();
    }

    @Test
    public void argsMatch() throws Exception {
        /**
         * String 타입의 파라미터를 허용 (String)
         */
        pointcut.setExpression("execution(* *(String))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    public void argsMatchNoArgs() throws Exception {
        /**
         * 파라미터가 없는 것 ()
         */
        pointcut.setExpression("execution(* *())");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    @Test
    public void argsMatchStar() throws Exception {
        /**
         * 정확히 하나의 파라미터 허용, 모든 타입 허용 (*)
         */
        pointcut.setExpression("execution(* *(*))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    public void argsMatchAll() throws Exception {
        /**
         * 숫자와 무관하게 모든 파라미터, 모든 타입 허용
         */
        pointcut.setExpression("execution(* *(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    public void argsMatchComplex() throws Exception {
        /**
         * String 타입으로 시작, 숫자와 무관하게 모든 파라미터, 모든 타입 허용
         * (String), (String, Xxx), (String, Xxx, Xxx)
         */
        pointcut.setExpression("execution(* *(String, ..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }
    
}
