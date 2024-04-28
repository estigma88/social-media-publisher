package com.coderstower.socialmediapubisher.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("itest")
public abstract class MockedEdgesConfig {
    @RegisterExtension
    static WireMockExtension wm1 = WireMockExtension.newInstance()
            .options(
                    wireMockConfig()
                            .port(8089)
                            .notifier(new SpringSlf4jNotifier())
            )
            .configureStaticDsl(true)
            .failOnUnmatchedRequests(true)
            .build();

    @LocalServerPort
    protected Integer port;
    private ObjectMapper mapper = new ObjectMapper();
    protected Path parentPath = Path.of("src/itest/resources/");

    protected void loadMocks(WireMockRuntimeInfo wireMockRuntimeInfo, String mocksPath) {
        var wireMock = wireMockRuntimeInfo.getWireMock();

        var mocksFolder = parentPath.resolve(mocksPath);

        wireMock.loadMappingsFrom(mocksFolder.toFile());
    }

    protected void validateResponse(String expectedResponsePath, String response) {
        try {
            var expectedTree = mapper.readTree(readFromFile(expectedResponsePath));
            var expectedJSONPretty = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(expectedTree);

            var currentTree = mapper.readTree(response);
            var currentJSONPretty = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(currentTree);

            assertEquals(expectedJSONPretty, currentJSONPretty);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private String readFromFile(String filePath) throws IOException {
        return Files.readString(parentPath.resolve(filePath));
    }
}
