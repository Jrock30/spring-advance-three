# Spring AOP

## 요약
> 애플리케이션 로직은 크게 핵심 기능과 부가 기능으로 나눌 수 있다.  
> 핵심 기능은 해당 객체가 제공하는 고유의 기능이다. 예를 들어서 OrderService 의 핵심 기능은 주문 로직이다.  
> 부가 기능은 핵심 기능을 보조하기 위해 제공되는 기능이다.   
> 예를 들어서 로그 추적 로직, 트랜잭션 기능이 있다. 이러한 부가 기능은 단독으로 사용되지 않고, 핵심 기능과 함께 사용된다.   
> 예를 들어서 로그 추적 기능은 어떤 핵심 기능이 호출되었는지 로그를 남기기 위해 사용한다. 그러니까 부가 기능은 이름 그대로 핵심 기능을 보조하기 위해 존재한다.
- Aspect
> 부가 기능과 부가 기능을 어디에 적용할지 선택하는 기능을 합해서 하나의 모듈로 만들었는데 이것이 바로 애스펙트(aspect)이다.  
> 애스펙트는 쉽게 이야기해서 부가 기능과, 해당 부가 기능을 어디에 적용할지 정의한 것이다.     
> 예를 들어서 로그 출력 기능을 모든 컨트롤러에 적용해라 라는 것이 정의되어 있다.  
> @Aspect 바로 그것이다.   
> 그리고 스프링이 제공하는 어드바이저도 어드바이스(부가 기능)과 포인트컷(적용 대상)을 가지고 있어서 개념상 하나의 애스펙트이다.
> 
> 애스펙트는 우리말로 해석하면 관점이라는 뜻인데,   
> 이름 그대로 애플리케이션을 바라보는 관점을 하나하나의 기능에서 횡단 관심사(cross-cutting concerns) 관점으로 달리 보는 것이다.   
> 이렇게 애스펙트를 사용한 프로그래밍 방식을 관점 지향 프로그래밍 AOP(Aspect-Oriented Programming)이라 한다.
> 
> 참고로 AOP 는 OOP 를 대체하기 위한 것이 아니라 횡단 관심사를 깔끔하게 처리하기 어려운 OOP 의 부족한 부분을 보조하는 목적으로 개발되었다.

- AspectJ 프레임워크는 스스로를 다음과 같이 설명한다.
  - 자바 프로그래밍 언어에 대한 완벽한 관점 지향 확장 
  - 횡단 관심사의 깔끔한 모듈화
    - 오류 검사 및 처리
    - 동기화
    - 성능 최적화(캐싱)
    - 모니터링 및 로깅
    
- AOP 적용방식
  - 컴파일 시점
  - 클래스 로딩 시점
  - 런타임 시점(프록시)


- AOP 적용위치
  - 적용 가능 지점(조인 포인트): 생성자, 필드 값 접근, static 메서드 접근, 메서드 실행
    - 이렇게 AOP를 적용할 수 있는 지점을 조인 포인트(Join point)라 한다.
  - AspectJ를 사용해서 컴파일 시점과 클래스 로딩 시점에 적용하는 AOP 는 바이트코드를 실제 조작하기 때문에 해당 기능을 모든 지점에 다 적용할 수 있다.
  - 프록시 방식을 사용하는 스프링 AOP 는 메서드 실행 지점에만 AOP 를 적용할 수 있다.
    - 프록시는 메서드 오버라이딩 개념으로 동작한다. 따라서 생성자나 static 메서드, 필드 값 접근에는 프록시 개념이 적용될 수 없다.
    - 프록시를 <b>사용하는 스프링 AOP 의 조인 포인트는 메서드 실행으로 제한된다.</b>
  - 프록시 방식을 사용하는 스프링 AOP 는 스프링 컨테이너가 관리할 수 있는 <b>스프링 빈에만 AOP</b> 를 적용할 수 있다.
  - 스프링은 AspectJ의 문법을 차용하고 프록시 방식의 AOP를 적용한다. AspectJ를 직접 사용하는 것이 아니다.

## 용어
- 조인 포인트(Join point)
  - 어드바이스가 적용될 수 있는 위치, 메소드 실행, 생성자 호출, 필드 값 접근, static 메서드 접근 같은 프로그램 실행 중 지점
  - 조인 포인트는 추상적인 개념이다. AOP를 적용할 수 있는 모든 지점이라 생각하면 된다.
  - 스프링 AOP 는 프록시 방식을 사용하므로 조인 포인트는 항상 메소드 실행 지점으로 제한된다.


- 포인트컷(Pointcut)
  - 조인 포인트 중에서 어드바이스가 적용될 위치를 선별하는 기능
  - 주로 AspectJ 표현식을 사용해서 지정
  - 프록시를 사용하는 스프링 AOP 는 메서드 실행 지점만 포인트컷으로 선별 가능


- 타켓(Target)
  - 어드바이스를 받는 객체, 포인트컷으로 결정


- 어드바이스(Advice)
  - 부가 기능
  - 특정 조인 포인트에서 Aspect에 의해 취해지는 조치
  - Around(주변), Before(전), After(후)와 같은 다양한 종류의 어드바이스가 있음 
  

- 애스펙트(Aspect)
  - 어드바이스 + 포인트컷을 모듈화 한 것 @Aspect 를 생각하면 됨
  - 여러 어드바이스와 포인트 컷이 함께 존재


- 어드바이저(Advisor)
  - 하나의 어드바이스와 하나의 포인트 컷으로 
  - 구성 스프링 AOP 에서만 사용되는 특별한 용어


- 위빙(Weaving)
  - 포인트컷으로 결정한 타켓의 조인 포인트에 어드바이스를 적용하는 것
  - 위빙을 통해 핵심 기능 코드에 영향을 주지 않고 부가 기능을 추가 할 수 있음
  - AOP 적용을 위해 애스펙트를 객체에 연결한 상태
    - 컴파일 타임(AspectJ compiler)
    - 로드 타임
    - 런타임, 스프링 AOP는 런타임, 프록시 방식


- AOP 프록시
  - AOP 기능을 구현하기 위해 만든 프록시 객체, 스프링에서 AOP 프록시는 JDK 동적 프록시 또는 CGLIB 프록시이다.
---

## Spring AOP 기능
 - @Aspect
 - @Order
 - @Pointcut (표현식 패턴)
 - Advice

### Advice 종류
- @Around : 메서드 호출 전후에 수행, 가장 강력한 어드바이스, 조인 포인트 실행 여부 선택, 반환 값 변환, 예외 변환 등이 가능
  - 아래의 4개를 한번에 사용한다고 보자.
- @Before : 조인 포인트(target) 실행 이전에 실행
- @AfterReturning : 조인 포인트(target)가 정상 완료후 실행 
- @AfterThrowing : 메서드가 예외를 던지는 경우 실행
- @After : 조인 포인트(target)가 정상 또는 예외에 관계없이 실행(finally)

## 포인트컷 지시자(Pointcut Designator)
 - 포인트컷 표현식은 execution 같은 포인트컷 지시자(Pointcut Designator)로 시작한다. 줄여서 PCD 라 한다.
 - 포인트컷 표현식은 AspectJ pointcut expression 즉 AspectJ 가 제공하는 포인트컷 표현식을 줄여서 말하는 것이다.
 - 포인트컷 지시자의 종류 종류
   - execution : 메소드 실행 조인 포인트를 매칭한다. 스프링 AOP에서 가장 많이 사용하고, 기능도 복잡하다.
     - execution(modifiers-pattern? ret-type-pattern declaring-type-pattern?name-pattern(param-pattern) throws-pattern?) -> ? 는 생략 가능
     - execution(접근제어자? 반환타입 선언타입?메서드이름(파라미터) 예외?)
     - ex) public java.lang.String hello.aop.member.MemberServiceImpl.hello(java.lang.String)
   - within : 특정 타입 내의 조인 포인트를 매칭한다.
   - args : 인자가 주어진 타입의 인스턴스인 조인 포인트
   - this : 스프링 빈 객체(스프링 AOP 프록시)를 대상으로 하는 조인 포인트
   - target : Target 객체(스프링 AOP 프록시가 가르키는 실제 대상)를 대상으로 하는 조인 포인트 
   - @target : 실행 객체의 클래스에 주어진 타입의 애노테이션이 있는 조인 포인트
   - @within : 주어진 애노테이션이 있는 타입 내 조인 포인트
   - @annotation : 메서드가 주어진 애노테이션을 가지고 있는 조인 포인트를 매칭
   - @args : 전달된 실제 인수의 런타임 타입이 주어진 타입의 애노테이션을 갖는 조인 포인트
   - bean : 스프링 전용 포인트컷 지시자, 빈의 이름으로 포인트컷을 지정한다.