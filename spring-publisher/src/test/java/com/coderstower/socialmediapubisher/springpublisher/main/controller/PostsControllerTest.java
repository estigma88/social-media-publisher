package com.coderstower.socialmediapubisher.springpublisher.main.controller;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.PostPublisher;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository.Post;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.Publication;
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
    public void ping(){
        Map<String, String> result = postsController.ping();

        Map<String, String> pong = new HashMap<>();
        pong.put("pong", "Hello, World!");

        assertThat(result).isEqualTo(pong);
    }

    @Test
    public void postNext() throws MalformedURLException {
        when(postPublisher.publishNext()).thenReturn(Post.builder()
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
                .build());

        Post result = postsController.postNext();

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
                .build());
    }

}