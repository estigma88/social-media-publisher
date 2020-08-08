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
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetSystemProperties;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("linkedin")
@SetSystemProperties({
        @SetSystemProperty(key = "sqlite4java.library.path", value = "target/native-libs"),
        @SetSystemProperty(key = "social-media-publisher.linkedin.access-token", value = "SETTOKEN")
})
class MockDynamoRealLinkedinTests {
    @Autowired
    private MockMvc mvc;

    @Test
    void publish_linkedin_success() throws Exception {
        mvc.perform(post("/posts/next")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("{\"id\":\"2\",\"name\":\"My Post 2\",\"description\":\"My second post\",\"tags\":[\"tag1\",\"tag2\"],\"url\":\"https://coderstower.com/2020/01/13/open-close-principle-by-example/\",\"publishedDate\":\"2020-03-03T05:06:08.000000001\",\"publications\":[{\"id\":\"shareid\",\"status\":\"SUCCESS\",\"publisher\":\"linkedin\",\"publishedDate\":\"2020-03-03T05:06:08.000000001\"}]}"));
    }

    @TestConfiguration
    static class OverriddenConfiguration {
        @Bean
        public Clock clock() {
            return Clock.fixed(ZonedDateTime.of(2020, 3, 3, 5, 6, 8, 1, ZoneId.of("UTC")).toInstant(), ZoneId.of("UTC"));
        }

        @Bean
        public AmazonDynamoDB amazonDynamoDB(@Value("${social-media-publisher.linkedin.access-token}") String accessToken) {
            AmazonDynamoDB ddb = DynamoDBEmbedded.create().amazonDynamoDB();

            createTable(ddb, "Oauth2Credentials", "id");
            createTable(ddb, "Posts", "id");

            PutItemRequest oauth2Credentials = new PutItemRequest();
            oauth2Credentials.setTableName("Oauth2Credentials");
            oauth2Credentials.addItemEntry("id", new AttributeValue("linkedin"));
            oauth2Credentials.addItemEntry("accessToken", new AttributeValue(accessToken));
            oauth2Credentials.addItemEntry("expirationDate", new AttributeValue("2020-04-01T05:05:05"));

            ddb.putItem(oauth2Credentials);

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
