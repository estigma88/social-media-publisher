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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    void publish() throws Exception {
        mvc.perform(post("/posts/next")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
