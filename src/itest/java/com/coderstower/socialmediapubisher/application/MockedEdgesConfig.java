package com.coderstower.socialmediapubisher.application;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WireMockTest(httpPort = 9090)
@ActiveProfiles("itest")
public abstract class MockedEdgesConfig {
    @Autowired
    protected WebTestClient webTestClient;
    @Autowired
    protected ResourceLoader resourceLoader;
}
