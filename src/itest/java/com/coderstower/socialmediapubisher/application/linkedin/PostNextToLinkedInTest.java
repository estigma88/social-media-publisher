package com.coderstower.socialmediapubisher.application.linkedin;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.coderstower.socialmediapubisher.application.MockedEdgesConfig;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static io.restassured.RestAssured.given;


public class PostNextToLinkedInTest extends MockedEdgesConfig {

    @Test
    public void publishNextPostSuccessful(WireMockRuntimeInfo wireMockRuntimeInfo) {
        loadMocks(wireMockRuntimeInfo, "testcases/post/next/linkedin/wiremock/");

        var response = given().
                port(port).
                post("/posts/group1/next").
                then().
                statusCode(200).
                extract()
                .body()
                .asString();

        validateResponse("testcases/post/next/linkedin/response.json", response);
    }

    @TestConfiguration
    static class OverriddenConfiguration {
        @Bean
        public Clock clock() {
            return Clock.fixed(ZonedDateTime.of(2020, 3, 3, 5, 6, 8, 1, ZoneId.of("UTC")).toInstant(), ZoneId.of("UTC"));
        }


        @Bean("amazonDynamoDB")
        public AmazonDynamoDB amazonDynamoDBLocal() {
            return AmazonDynamoDBClientBuilder.standard()
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8089", Regions.US_EAST_1.getName()))
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("132", "456")))
                    .build();
        }
    }
}
