package com.coderstower.socialmediapubisher.extesion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ITestHandler {
    private ObjectMapper mapper = new ObjectMapper();
    private Path parentPath = Path.of("src/itest/resources/");

    public void loadWiremockMocks(WireMockRuntimeInfo wireMockRuntimeInfo, String mocksPath) {
        var wireMock = wireMockRuntimeInfo.getWireMock();

        var mocksFolder = parentPath.resolve(mocksPath);

        wireMock.loadMappingsFrom(mocksFolder.toFile());
    }

    public void validateJSONResponse(String expectedResponsePath, String response) {
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
