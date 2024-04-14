package com.coderstower.socialmediapubisher.main.controller;

import com.coderstower.socialmediapubisher.domain.post.PostPublisher;
import com.coderstower.socialmediapubisher.domain.post.repository.Post;
import com.coderstower.socialmediapubisher.domain.post.socialmedia.Publication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostsControllerTest {
    @Mock
    private PostPublisher postPublisher;
    @InjectMocks
    private PostsController postsController;

    @Test
    public void ping() {
        Map<String, String> result = postsController.ping();

        Map<String, String> pong = new HashMap<>();
        pong.put("pong", "Hello, World!");

        assertThat(result).isEqualTo(pong);
    }

    @Test
    public void postNext() throws MalformedURLException {
        when(postPublisher.publishNext("group1")).thenReturn(Post.builder()
                .id("2")
                .name("My Post 2")
                .description("My second post")
                .tags(List.of("tag1", "tag2"))
                .url(URI.create("https://coderstower.com/2020/01/13/open-close-principle-by-example/").toURL())
                .publishedDate(LocalDateTime.parse("2012-09-17T18:47:52"))
                .publications(List.of(Publication.builder()
                        .status(Publication.Status.FAILURE)
                        .publisher("twitter")
                        .build()))
                .group("group1")
                .build());

        Post result = postsController.postNext("group1");

        assertThat(result).isEqualTo(Post.builder()
                .id("2")
                .name("My Post 2")
                .description("My second post")
                .tags(List.of("tag1", "tag2"))
                .url(URI.create("https://coderstower.com/2020/01/13/open-close-principle-by-example/").toURL())
                .publishedDate(LocalDateTime.parse("2012-09-17T18:47:52"))
                .publications(List.of(Publication.builder()
                        .status(Publication.Status.FAILURE)
                        .publisher("twitter")
                        .build()))
                .group("group1")
                .build());
    }

}