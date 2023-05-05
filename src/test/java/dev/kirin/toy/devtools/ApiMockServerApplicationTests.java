package dev.kirin.toy.devtools;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"spring.profiles.active=test"})
class ApiMockServerApplicationTests {

    @Test
    void contextLoads() {
    }

}
