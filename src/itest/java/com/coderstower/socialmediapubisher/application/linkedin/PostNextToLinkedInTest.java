package com.coderstower.socialmediapubisher.application.linkedin;

import com.coderstower.socialmediapubisher.application.MockedEdgesConfig;
import com.coderstower.socialmediapubisher.extesion.ITestHandler;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;


public class PostNextToLinkedInTest extends MockedEdgesConfig {

    @Test
    public void publishNextPostSuccessful(WireMockRuntimeInfo wireMockRuntimeInfo, ITestHandler iTestHandler) {
        iTestHandler.loadWiremockMocks(wireMockRuntimeInfo, "testcases/post/next/linkedin/wiremock/");

        var response = given().
                port(port).
                post("/posts/group1/next").
                then().
                statusCode(200).
                extract()
                .body()
                .asString();

        iTestHandler.validateJSONResponse("testcases/post/next/linkedin/response.json", response);

        //TODO validate updated publish date
    }
}
