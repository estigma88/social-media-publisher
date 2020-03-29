package com.coderstower.socialmediapubisher.springpublisher.main;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
class RealSocialMediaTests {
    @Autowired
    private MockMvc mvc;

    @Test
    void ping() throws Exception {
        mvc.perform(get("/ping")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pong", is("Hello, World!")));
    }

    @Test
    void publish_twitter_success() throws Exception {
        mvc.perform(post("/posts/next")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("{\"id\":\"2\",\"name\":\"My Post 2\",\"description\":\"My second post\",\"tags\":[\"tag1\",\"tag2\"],\"url\":\"https://coderstower.com/2020/01/13/open-close-principle-by-example/\",\"publishedDate\":\"2020-03-03T05:06:08.000000001\",\"publications\":[{\"status\":\"SUCCESS\",\"publisher\":\"twitter\",\"publishedDate\":\"2020-03-03T05:06:08.000000001\"}]}"));
    }

    @TestConfiguration
    static class OverriddenConfiguration {
        @Bean
        public Clock clock() {
            return Clock.fixed(ZonedDateTime.of(2020, 3, 3, 5, 6, 8, 1, ZoneId.of("UTC")).toInstant(), ZoneId.of("UTC"));
        }
    }

}
