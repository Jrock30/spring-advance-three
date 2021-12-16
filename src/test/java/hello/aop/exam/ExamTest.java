package hello.aop.exam;

import hello.aop.exam.aop.RetryAspect;
import hello.aop.exam.aop.TraceAspect;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Slf4j
//@Import(TraceAspect.class)
@Import({TraceAspect.class, RetryAspect.class})
@SpringBootTest // 스프링 컨테이너 기동
public class ExamTest {

    @Autowired
    ExamService examService;

    @Test
    public void test() throws Exception {
        for (int i = 0; i < 5; i++) {
            examService.request("data" + i);
            log.info("client request i={}", i);
        }
    }
}
