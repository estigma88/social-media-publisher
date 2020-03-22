package com.coderstower.socialmediapubisher.springpublisher.main.aws;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.coderstower.socialmediapubisher.springpublisher.main.aws.repository.oauth1.Oauth1CredentialAWSRepository;
import com.coderstower.socialmediapubisher.springpublisher.main.aws.repository.oauth1.Oauth1CredentialDynamoRepository;
import com.coderstower.socialmediapubisher.springpublisher.main.aws.repository.post.PostAWSRepository;
import com.coderstower.socialmediapubisher.springpublisher.main.aws.repository.post.PostDynamoRepository;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@EnableDynamoDBRepositories
        (basePackages = "com.coderstower.socialmediapubisher.springpublisher.main.aws.repository")
public class SpringPublisherDynamoDBRepositoryFactory {
    @Bean
    public AmazonDynamoDB amazonDynamoDB(@Value("${amazon.dynamodb.endpoint}") String amazonDynamoDBEndpoint) {
        AmazonDynamoDB amazonDynamoDB
                = new AmazonDynamoDBClient();

        if (!StringUtils.isEmpty(amazonDynamoDBEndpoint)) {
            amazonDynamoDB.setEndpoint(amazonDynamoDBEndpoint);
        }

        return amazonDynamoDB;
    }

    @Bean
    public Oauth1CredentialAWSRepository oauth1CredentialAWSRepository(Oauth1CredentialDynamoRepository oauth1CredentialDynamoRepository) {
        return new Oauth1CredentialAWSRepository(oauth1CredentialDynamoRepository);
    }

    @Bean
    public PostAWSRepository postAWSRepository(PostDynamoRepository postDynamoRepository) {
        return new PostAWSRepository(postDynamoRepository);
    }
}
