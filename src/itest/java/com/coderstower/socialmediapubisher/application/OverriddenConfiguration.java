package com.coderstower.socialmediapubisher.application;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
@TestConfiguration
public class OverriddenConfiguration {
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
