package hello.aop.order.aop;

import org.aspectj.lang.annotation.Pointcut;

public class PointCuts {

    // hello.aop.order 패키지와 하위 패키지
    @Pointcut("execution(* hello.aop.order..*(..))")
    public void allOrder() {
    } // pointcut signature

    // 클래스 이름 패턴이 *Service ( 보통의 트랜잭션 이라고 생각하고 구현 )
    @Pointcut("execution(* *..*Service.*(..))")
    public void allService() {
    }

    // allOrder && allService
    @Pointcut("allOrder() && allService()")
    public void orderAndService(){}
}
