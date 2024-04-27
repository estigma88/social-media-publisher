package com.coderstower.socialmediapubisher.application.aws;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.model.*;
import com.coderstower.socialmediapubisher.application.socialmedia.linkedin.*;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import twitter4j.*;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@SetSystemProperty(key = "sqlite4java.library.path", value = "target/native-libs")
@ActiveProfiles({"linkedin", "twitter"})
class MockSocialMediaSuccessfulTests {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private Twitter twitter;
    @MockBean
    private RestTemplate restTemplate;

    @Test
    void ping() throws Exception {
        mvc.perform(get("/ping")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pong", is("Hello, World!")));
    }

    @Test
    void publish_allSocialMedia_success() throws Exception {
        mockingTwitter();
        mockingLinkedIn();

        mvc.perform(post("/posts/group1/next")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("{\"id\":\"2\",\"name\":\"My Post 2\",\"description\":\"My second post\",\"tags\":[\"tag1\",\"tag2\"],\"url\":\"https://coderstower.com/2020/01/13/open-close-principle-by-example/\",\"publishedDate\":\"2020-03-03T05:06:08.000000001\",\"publications\":[{\"id\":\"123\",\"status\":\"SUCCESS\",\"publisher\":\"twitter\",\"publishedDate\":\"2020-03-03T05:06:08.000000001\"},{\"id\":\"shareid\",\"status\":\"SUCCESS\",\"publisher\":\"linkedin\",\"publishedDate\":\"2020-03-03T05:06:08.000000001\"}]}"));
    }

    private void mockingLinkedIn() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("X-Restli-Protocol-Version", "2.0.0");
        httpHeaders.add("LinkedIn-Version", "202304");
        httpHeaders.setBearerAuth("access123");

        HttpEntity<Void> requestMe = new HttpEntity<>(httpHeaders);

        when(restTemplate.exchange("https://api.linkedin.com/v2/userinfo", HttpMethod.GET, requestMe, Profile.class))
                .thenReturn(ResponseEntity.ok(Profile.builder()
                        .sub("memberid")
                        .build()));

        LinkedInShare linkedInShare = LinkedInShare.builder()
                .author("urn:li:person:memberid")
                .lifecycleState("PUBLISHED")
                .commentary("My second post\n\n#tag1 #tag2")
                .distribution(Distribution.builder().feedDistribution("MAIN_FEED").build())
                .lifecycleState("PUBLISHED")
                .content(Content.builder()
                        .article(ArticleContent.builder()
                                .description("My second post")
                                .title("My Post 2")
                                .source("https://coderstower.com/2020/01/13/open-close-principle-by-example/")
                                .build())
                        .build())
                .visibility("PUBLIC")
                .build();

        HttpEntity<LinkedInShare> requestShare = new HttpEntity<>(linkedInShare, httpHeaders);

        when(restTemplate.exchange("https://api.linkedin.com/rest/posts", HttpMethod.POST, requestShare, Void.class))
                .thenReturn(ResponseEntity.ok()
                        .header("X-RestLi-Id", "shareid")
                        .build());
    }

    private void mockingTwitter() throws TwitterException {
        Paging paging = new Paging(1, 1);
        when(twitter.getHomeTimeline(paging)).thenReturn(mock(ResponseList.class));

        Status status = mock(Status.class);
        when(status.getId()).thenReturn(123L);
        when(twitter.updateStatus("My second post\n\n#tag1 #tag2\n\nhttps://coderstower.com/2020/01/13/open-close-principle-by-example/")).thenReturn(status);
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
            createTable(ddb, "Posts", "id");

            PutItemRequest oauth2Credentials = new PutItemRequest();
            oauth2Credentials.setTableName("Oauth2Credentials");
            oauth2Credentials.addItemEntry("id", new AttributeValue("linkedin"));
            oauth2Credentials.addItemEntry("accessToken", new AttributeValue("access123"));
            oauth2Credentials.addItemEntry("allowedGroups", new AttributeValue().withL(List.of(new AttributeValue("group1"))));
            oauth2Credentials.addItemEntry("expirationDate", new AttributeValue("2020-04-01T05:05:05"));

            ddb.putItem(oauth2Credentials);

            PutItemRequest oauth1Credentials = new PutItemRequest();
            oauth1Credentials.setTableName("Oauth1Credentials");
            oauth1Credentials.addItemEntry("id", new AttributeValue("twitter"));
            oauth1Credentials.addItemEntry("accessToken", new AttributeValue("access123"));
            oauth1Credentials.addItemEntry("consumerKey", new AttributeValue("consumer123"));
            oauth1Credentials.addItemEntry("consumerSecret", new AttributeValue("csecret123"));
            oauth1Credentials.addItemEntry("tokenSecret", new AttributeValue("tsecret123"));

            ddb.putItem(oauth1Credentials);

            PutItemRequest post1 = new PutItemRequest();
            post1.setTableName("Posts");
            post1.addItemEntry("id", new AttributeValue("1"));
            post1.addItemEntry("name", new AttributeValue("My Post 1"));
            post1.addItemEntry("description", new AttributeValue("My first post"));
            post1.addItemEntry("tags", new AttributeValue().withL(new AttributeValue("tag1"), new AttributeValue("tag2")));
            post1.addItemEntry("url", new AttributeValue("https://coderstower.com/2020/02/18/unit-tests-vs-integration-tests/"));
            post1.addItemEntry("publishedDate", new AttributeValue("2013-09-17T18:47:52"));
            post1.addItemEntry("group", new AttributeValue("group1"));

            ddb.putItem(post1);

            PutItemRequest post2 = new PutItemRequest();
            post2.setTableName("Posts");
            post2.addItemEntry("id", new AttributeValue("2"));
            post2.addItemEntry("name", new AttributeValue("My Post 2"));
            post2.addItemEntry("description", new AttributeValue("My second post"));
            post2.addItemEntry("tags", new AttributeValue().withL(new AttributeValue("tag1"), new AttributeValue("tag2")));
            post2.addItemEntry("url", new AttributeValue("https://coderstower.com/2020/01/13/open-close-principle-by-example/"));
            post2.addItemEntry("publishedDate", new AttributeValue("2012-09-17T18:47:52"));
            post2.addItemEntry("group", new AttributeValue("group1"));

            ddb.putItem(post2);

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
