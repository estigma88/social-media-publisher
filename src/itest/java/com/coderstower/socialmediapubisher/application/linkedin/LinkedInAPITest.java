package com.coderstower.socialmediapubisher.application.linkedin;

import com.coderstower.socialmediapubisher.application.socialmedia.linkedin.LinkedInPublisher;
import com.coderstower.socialmediapubisher.domain.security.repository.OAuth2Credentials;
import com.coderstower.socialmediapubisher.extesion.ITestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"read-only", "linkedin"})
@ExtendWith(ITestExtension.class)
public class LinkedInAPITest {
    @Autowired
    private LinkedInPublisher linkedInPublisher;

    @Test
    public void getProfile() {
        var credentials = OAuth2Credentials.builder().accessToken("xxxxxxxx").build();

        var profile = linkedInPublisher.getProfile(credentials);

        assertNotNull(profile);
    }
}
