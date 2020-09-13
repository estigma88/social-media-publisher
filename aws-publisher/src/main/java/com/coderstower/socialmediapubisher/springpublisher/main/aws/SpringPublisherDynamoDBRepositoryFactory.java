package com.coderstower.socialmediapubisher.springpublisher.main.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.coderstower.socialmediapubisher.springpublisher.main.aws.repository.oauth1.OAuth1CredentialAWSRepository;
import com.coderstower.socialmediapubisher.springpublisher.main.aws.repository.oauth1.OAuth1CredentialDynamoRepository;
import com.coderstower.socialmediapubisher.springpublisher.main.aws.repository.oauth2.OAuth2CredentialAWSRepository;
import com.coderstower.socialmediapubisher.springpublisher.main.aws.repository.oauth2.OAuth2CredentialDynamoRepository;
import com.coderstower.socialmediapubisher.springpublisher.main.aws.repository.post.PostAWSRepository;
import com.coderstower.socialmediapubisher.springpublisher.main.aws.repository.post.PostDynamoRepository;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableDynamoDBRepositories
        (basePackages = "com.coderstower.socialmediapubisher.springpublisher.main.aws.repository")
public class SpringPublisherDynamoDBRepositoryFactory {
  @Bean("amazonDynamoDB")
  @Profile("!local")
  public AmazonDynamoDB amazonDynamoDB() {
    return AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.US_EAST_1)
            .build();
  }

  @Bean("amazonDynamoDB")
  @Profile("local")
  public AmazonDynamoDB amazonDynamoDBLocal(@Value("${amazon.dynamodb.secretkey}") String amazonAWSSecretKey,
                                            @Value("${amazon.dynamodb.accesskey}") String amazonAWSAccessKey,
                                            @Value("${amazon.dynamodb.endpoint}") String amazonAWSEndpoint) {
    return AmazonDynamoDBClientBuilder.standard()
            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(amazonAWSEndpoint, Regions.US_EAST_1.getName()))
            .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(amazonAWSAccessKey, amazonAWSSecretKey)))
            .build();
  }

  @Bean
  public OAuth1CredentialAWSRepository oauth1CredentialAWSRepository(
          OAuth1CredentialDynamoRepository oauth1CredentialDynamoRepository) {
    return new OAuth1CredentialAWSRepository(
            oauth1CredentialDynamoRepository);
  }

  @Bean
  public OAuth2CredentialAWSRepository oauth2CredentialAWSRepository(
          OAuth2CredentialDynamoRepository oauth2CredentialDynamoRepository) {
    return new OAuth2CredentialAWSRepository(
            oauth2CredentialDynamoRepository);
  }

  @Bean
  public PostAWSRepository postAWSRepository(
          PostDynamoRepository postDynamoRepository) {
    return new PostAWSRepository(postDynamoRepository);
  }
}
