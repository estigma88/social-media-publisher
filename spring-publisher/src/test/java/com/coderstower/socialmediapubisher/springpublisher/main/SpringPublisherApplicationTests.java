package com.coderstower.socialmediapubisher.springpublisher.main;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SpringPublisherApplicationTests {
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
                        .json("[{\"post\":{\"id\":\"1\",\"name\":\"My Post 1\",\"description\":\"My first post\",\"tags\":[\"tag1\",\"tag2\"],\"url\":\"http://mypost.com/post1\",\"lastDatePublished\":\"2012-09-17T18:47:52\"},\"status\":\"SUCCESS\",\"publisher\":\"twitter\"}]"));
    }

}
