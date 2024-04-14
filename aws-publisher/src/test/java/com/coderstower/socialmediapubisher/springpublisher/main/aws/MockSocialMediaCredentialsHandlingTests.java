package com.coderstower.socialmediapubisher.springpublisher.main.aws;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@SetSystemProperty(key = "sqlite4java.library.path", value = "target/native-libs")
@TestPropertySource(properties = {"social-media-publisher.principal-names-allowed.linkedin=myuser"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles({"linkedin", "twitter, secure"})
class MockSocialMediaCredentialsHandlingTests {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private Twitter twitter;
    @MockBean
    private OAuth2AuthorizedClientService authorizedClientService;
    @Autowired
    private ClientRegistrationRepository registrations;

    @Test
    @Order(1)
    void publish_credentialsExpired_unauthorizedException() throws Exception {
        mockingTwitter();

        mvc.perform(post("/posts/group1/next")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unauthorized for linkedin credential1. Please login again here: http://localhost:8080/oauth2/linkedin/credentials"));
    }

    @Test
    @Order(2)
    void login_noSuccessLinkedin_return302() throws Exception {
        mvc.perform(get("/oauth2/linkedin/credentials")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "http://localhost/oauth2/authorization/linkedin"));
    }

    @Test
    @Order(3)
    void login_successLinkedin_updateCredentials() throws Exception {
        mockingTwitter();

        OAuth2AuthenticationToken authenticationToken = createToken();
        OAuth2AuthorizedClient authorizedClient = createAuthorizedClient(authenticationToken);

        when(this.authorizedClientService.loadAuthorizedClient(eq("linkedin"), anyString()))
                .thenReturn(authorizedClient);

        mvc.perform(get("/oauth2/linkedin/credentials")
                .with(authentication(authenticationToken))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private OAuth2AuthorizedClient createAuthorizedClient(OAuth2AuthenticationToken authenticationToken) {
        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "newAccessToken",
                LocalDateTime.of(2020, 3, 3, 5, 6, 8, 1).toInstant(ZoneOffset.UTC),
                LocalDateTime.of(2020, 5, 3, 5, 6, 8, 1).toInstant(ZoneOffset.UTC));

        ClientRegistration clientRegistration = this.registrations.findByRegistrationId(authenticationToken.getAuthorizedClientRegistrationId());
        return new OAuth2AuthorizedClient(clientRegistration, authenticationToken.getName(), accessToken);
    }

    private OAuth2AuthenticationToken createToken() {
        Set<GrantedAuthority> authorities = new HashSet<>(AuthorityUtils.createAuthorityList("USER"));
        OAuth2User oAuth2User = new DefaultOAuth2User(authorities, Collections.singletonMap("id", "myuser"), "id");
        return new OAuth2AuthenticationToken(oAuth2User, authorities, "linkedin");
    }

    private void mockingTwitter() throws TwitterException {
        Paging paging = new Paging(1, 1);
        when(twitter.getHomeTimeline(paging)).thenReturn(mock(ResponseList.class));
    }

    @TestConfiguration
    static class OverriddenConfiguration {
        @Bean
        public Clock clock() {
            return Clock.fixed(ZonedDateTime.of(2020, 3, 3, 5, 6, 8, 1, ZoneId.of("UTC")).toInstant(), ZoneId.of("UTC"));
        }

        @Bean
        public AmazonDynamoDB amazonDynamoDB() {
            AmazonDynamoDB ddb = DynamoDBEmbedded.create().amazonDynamoDB();

            createTable(ddb, "Oauth1Credentials", "id");
            createTable(ddb, "Oauth2Credentials", "id");

            PutItemRequest oauth2Credentials = new PutItemRequest();
            oauth2Credentials.setTableName("Oauth2Credentials");
            oauth2Credentials.addItemEntry("id", new AttributeValue("credential1"));
            oauth2Credentials.addItemEntry("accessToken", new AttributeValue("access123"));
            oauth2Credentials.addItemEntry("expirationDate", new AttributeValue("2020-02-01T05:05:05"));

            ddb.putItem(oauth2Credentials);

            PutItemRequest oauth1Credentials = new PutItemRequest();
            oauth1Credentials.setTableName("Oauth1Credentials");
            oauth1Credentials.addItemEntry("id", new AttributeValue("twitter"));
            oauth1Credentials.addItemEntry("accessToken", new AttributeValue("access123"));
            oauth1Credentials.addItemEntry("consumerKey", new AttributeValue("consumer123"));
            oauth1Credentials.addItemEntry("consumerSecret", new AttributeValue("csecret123"));
            oauth1Credentials.addItemEntry("tokenSecret", new AttributeValue("tsecret123"));

            ddb.putItem(oauth1Credentials);

            return ddb;
        }

        private CreateTableResult createTable(AmazonDynamoDB ddb, String tableName, String hashKeyName) {
            List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
            attributeDefinitions.add(new AttributeDefinition(hashKeyName, ScalarAttributeType.S));

            List<KeySchemaElement> ks = new ArrayList<>();
            ks.add(new KeySchemaElement(hashKeyName, KeyType.HASH));

            ProvisionedThroughput provisionedthroughput = new ProvisionedThroughput(1000L, 1000L);

            CreateTableRequest request =
                    new CreateTableRequest()
                            .withTableName(tableName)
                            .withAttributeDefinitions(attributeDefinitions)
                            .withKeySchema(ks)
                            .withProvisionedThroughput(provisionedthroughput);

            return ddb.createTable(request);
        }
    }

}
