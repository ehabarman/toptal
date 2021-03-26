package com.toptal.backend;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
class ToptalBackendApplicationTests {

    @Test
    void main() {
        ToptalBackendApplication application = new ToptalBackendApplication();
        application.main(new String[0]);
    }

}
