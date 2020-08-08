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
import com.coderstower.socialmediapubisher.springpublisher.main.socialmedia.linkedin.LinkedInShare;
import com.coderstower.socialmediapubisher.springpublisher.main.socialmedia.linkedin.Media;
import com.coderstower.socialmediapubisher.springpublisher.main.socialmedia.linkedin.Profile;
import com.coderstower.socialmediapubisher.springpublisher.main.socialmedia.linkedin.ShareContent;
import com.coderstower.socialmediapubisher.springpublisher.main.socialmedia.linkedin.SpecificContent;
import com.coderstower.socialmediapubisher.springpublisher.main.socialmedia.linkedin.Text;
import com.coderstower.socialmediapubisher.springpublisher.main.socialmedia.linkedin.Visibility;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@SetSystemProperty(key = "sqlite4java.library.path", value = "target/native-libs")
class MockDynamoSocialMediaTests {
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

        mvc.perform(post("/posts/next")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("{\"id\":\"2\",\"name\":\"My Post 2\",\"description\":\"My second post\",\"tags\":[\"tag1\",\"tag2\"],\"url\":\"https://coderstower.com/2020/01/13/open-close-principle-by-example/\",\"publishedDate\":\"2020-03-03T05:06:08.000000001\",\"publications\":[{\"id\":\"123\",\"status\":\"SUCCESS\",\"publisher\":\"twitter\",\"publishedDate\":\"2020-03-03T05:06:08.000000001\"},{\"id\":\"shareid\",\"status\":\"SUCCESS\",\"publisher\":\"linkedin\",\"publishedDate\":\"2020-03-03T05:06:08.000000001\"}]}"));
    }

    private void mockingLinkedIn() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("X-Restli-Protocol-Version", "2.0.0");
        httpHeaders.setBearerAuth("access123");

        HttpEntity<Void> requestMe = new HttpEntity<>(httpHeaders);

        when(restTemplate.exchange("https://api.linkedin.com/v2/me", HttpMethod.GET, requestMe, Profile.class))
                .thenReturn(ResponseEntity.ok(Profile.builder()
                        .id("memberid")
                        .build()));

        LinkedInShare linkedInShare = LinkedInShare.builder()
                .author("urn:li:person:memberid")
                .lifecycleState("PUBLISHED")
                .specificContent(SpecificContent.builder()
                        .shareContent(ShareContent.builder()
                                .shareCommentary(Text.builder()
                                        .text("My second post\n\n#tag1 #tag2")
                                        .build())
                                .shareMediaCategory("ARTICLE")
                                .media(Media.builder()
                                        .description(Text.builder()
                                                .text("My second post")
                                                .build())
                                        .title(Text.builder()
                                                .text("My Post 2")
                                                .build())
                                        .status("READY")
                                        .originalUrl("https://coderstower.com/2020/01/13/open-close-principle-by-example/")
                                        .build())
                                .build())
                        .build())
                .visibility(Visibility.builder()
                        .memberNetworkVisibility("PUBLIC")
                        .build())
                .build();

        HttpEntity<LinkedInShare> requestShare = new HttpEntity<>(linkedInShare, httpHeaders);

        when(restTemplate.exchange("https://api.linkedin.com/v2/ugcPosts", HttpMethod.POST, requestShare, Void.class))
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

            ddb.putItem(post1);

            PutItemRequest post2 = new PutItemRequest();
            post2.setTableName("Posts");
            post2.addItemEntry("id", new AttributeValue("2"));
            post2.addItemEntry("name", new AttributeValue("My Post 2"));
            post2.addItemEntry("description", new AttributeValue("My second post"));
            post2.addItemEntry("tags", new AttributeValue().withL(new AttributeValue("tag1"), new AttributeValue("tag2")));
            post2.addItemEntry("url", new AttributeValue("https://coderstower.com/2020/01/13/open-close-principle-by-example/"));
            post2.addItemEntry("publishedDate", new AttributeValue("2012-09-17T18:47:52"));

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
