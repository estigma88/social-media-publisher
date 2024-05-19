package com.coderstower.socialmediapubisher.application.linkedin;

import com.coderstower.socialmediapubisher.extesion.ITestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("read-only")
@ExtendWith(ITestExtension.class)
public class ReadOnlyNoPublishTest {

    @Test
    public void testContext() {

    }
}
