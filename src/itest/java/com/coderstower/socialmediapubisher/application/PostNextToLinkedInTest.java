package com.coderstower.socialmediapubisher.application;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;


public class PostNextToLinkedInTest extends MockedEdgesConfig{

    @Test
    public void publishNextPostSuccessful(WireMockRuntimeInfo wireMockRuntimeInfo) throws IOException {
        var wireMock = wireMockRuntimeInfo.getWireMock();

        var resource = resourceLoader.getResource("classpath:testcases/post/next/linkedin/wiremock/");

        wireMock.loadMappingsFrom(resource.getFile());
    }
}
