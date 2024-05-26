package com.coderstower.socialmediapubisher.application.linkedin;

import com.coderstower.socialmediapubisher.application.MockedEdgesConfig;
import com.coderstower.socialmediapubisher.extesion.ITestHandler;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;

import static org.mockito.Mockito.*;

import static io.restassured.RestAssured.given;


public class PostNextToLinkedInTest extends MockedEdgesConfig {

    @Test
    public void publishNextPostSuccessful(WireMockRuntimeInfo wireMockRuntimeInfo, ITestHandler iTestHandler) {
        iTestHandler.loadWiremockMocks(wireMockRuntimeInfo, "testcases/post/next/linkedin/success/wiremock/");

        var response = given().
                port(port).
                post("/posts/group1/next").
                then().
                statusCode(200).
                extract()
                .body()
                .asString();

        iTestHandler.validateJSONResponse("testcases/post/next/linkedin/success/response.json", response);

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo("receiver@mail.com");
        simpleMailMessage.setFrom("sender@mail.com");
        simpleMailMessage.setSubject("SocialMediaPublisher: Success publishing group group1");
        simpleMailMessage.setText("Post(id=post1, name=Post 1, description=This is a new post 1, tags=[java, code], url=http://coders.com/post1, publishedDate=2020-03-03T05:06:08.000000001, publications=[Publication(id=linkedIn123, status=SUCCESS, publisher=linkedin, credentialId=id132, publishedDate=2020-03-03T05:06:08.000000001)], group=group1)");

        verify(mailSender).send(simpleMailMessage);
    }

    @Test
    public void publishNextPostFail(WireMockRuntimeInfo wireMockRuntimeInfo, ITestHandler iTestHandler) {
        iTestHandler.loadWiremockMocks(wireMockRuntimeInfo, "testcases/post/next/linkedin/failureExpiredCredentials/wiremock/");

        var response = given().
                port(port).
                post("/posts/group1/next").
                then().
                statusCode(401).
                extract()
                .body()
                .asString();

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo("receiver@mail.com");
        simpleMailMessage.setFrom("sender@mail.com");
        simpleMailMessage.setSubject("SocialMediaPublisher: Error publishing group group1");
        simpleMailMessage.setText("Unauthorized for linkedin id132. Please login again here: http://localhost:8080/oauth2/linkedin/credentials");

        verify(mailSender).send(simpleMailMessage);
    }
}
