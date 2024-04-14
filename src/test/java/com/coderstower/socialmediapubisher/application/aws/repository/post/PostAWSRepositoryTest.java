package com.coderstower.socialmediapubisher.application.aws.repository.post;

import com.coderstower.socialmediapubisher.domain.post.repository.Post;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostAWSRepositoryTest {
    @Mock
    private PostDynamoRepository postDynamoRepository;
    @InjectMocks
    private PostAWSRepository postAWSRepository;

    @Test
    public void getNextToPublish() throws MalformedURLException {
        when(postDynamoRepository.findAll()).thenReturn(List.of(PostDynamo.builder()
                .id("2")
                .name("My Post 2")
                .description("My second post")
                .tags(List.of("tag1", "tag2"))
                .group("group1")
                .url(URI.create("https://coderstower.com/2020/01/13/open-close-principle-by-example/").toURL())
                .publishedDate(LocalDateTime.parse("2012-09-17T18:47:52"))
                .build(), PostDynamo.builder()
                .id("1")
                .name("My Post 1")
                .description("My first post")
                .tags(List.of("tag1", "tag2"))
                .group("group1")
                .url(URI.create("https://coderstower.com/2020/02/18/unit-tests-vs-integration-tests/").toURL())
                .publishedDate(LocalDateTime.parse("2013-09-17T18:47:52"))
                .build()));

        Optional<Post> post = postAWSRepository.getNextToPublish("group1");

        assertThat(post).isEqualTo(Optional.of(Post.builder()
                .id("2")
                .name("My Post 2")
                .description("My second post")
                .tags(List.of("tag1", "tag2"))
                .url(URI.create("https://coderstower.com/2020/01/13/open-close-principle-by-example/").toURL())
                .publishedDate(LocalDateTime.parse("2012-09-17T18:47:52"))
                .group("group1")
                .build()));
    }

    @Test
    public void update() throws MalformedURLException {
        when(postDynamoRepository.save(PostDynamo.builder()
                .id("2")
                .name("My Post 2")
                .description("My second post")
                .tags(List.of("tag1", "tag2"))
                .url(URI.create("https://coderstower.com/2020/01/13/open-close-principle-by-example/").toURL())
                .publishedDate(LocalDateTime.parse("2012-09-17T18:47:52"))
                .build()))
                .thenReturn(PostDynamo.builder()
                        .id("2")
                        .name("My Post 2")
                        .description("My second post")
                        .tags(List.of("tag1", "tag2"))
                        .url(URI.create("https://coderstower.com/2020/01/13/open-close-principle-by-example/").toURL())
                        .publishedDate(LocalDateTime.parse("2012-09-17T18:47:52"))
                        .build());

        Post post = postAWSRepository.update(Post.builder()
                .id("2")
                .name("My Post 2")
                .description("My second post")
                .tags(List.of("tag1", "tag2"))
                .url(URI.create("https://coderstower.com/2020/01/13/open-close-principle-by-example/").toURL())
                .publishedDate(LocalDateTime.parse("2012-09-17T18:47:52"))
                .build());

        assertThat(post).isEqualTo(Post.builder()
                .id("2")
                .name("My Post 2")
                .description("My second post")
                .tags(List.of("tag1", "tag2"))
                .url(URI.create("https://coderstower.com/2020/01/13/open-close-principle-by-example/").toURL())
                .publishedDate(LocalDateTime.parse("2012-09-17T18:47:52"))
                .build());
    }

}