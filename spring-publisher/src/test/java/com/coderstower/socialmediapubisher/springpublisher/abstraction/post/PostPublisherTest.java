package com.coderstower.socialmediapubisher.springpublisher.abstraction.post;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository.Post;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository.PostRepository;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.Acknowledge;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.Publication;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.SocialMediaPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostPublisherTest {
    @Mock
    private SocialMediaPublisher socialMediaPublisher1;
    @Mock
    private SocialMediaPublisher socialMediaPublisher2;
    @Mock
    private PostRepository postRepository;
    private PostPublisher postPublisher;

    @BeforeEach
    public void before() {
        this.postPublisher = new PostPublisher(List.of(socialMediaPublisher1, socialMediaPublisher2), postRepository, Clock
                .fixed(ZonedDateTime.of(2020, 3, 3, 5, 6, 8, 1, ZoneId.of("UTC")).toInstant(), ZoneId.of("UTC")));
    }

    @Test
    public void publishNext_pingFailed_exception() {
        when(socialMediaPublisher1.ping()).thenReturn(Acknowledge.builder()
                .status(Acknowledge.Status.SUCCESS).build());
        when(socialMediaPublisher2.ping()).thenReturn(Acknowledge.builder()
                .status(Acknowledge.Status.FAILURE).build());
        when(socialMediaPublisher2.getName()).thenReturn("LINKEDIN");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> postPublisher.publishNext());

        assertThat(exception.getMessage()).isEqualTo("Ping to LINKEDIN failed: " + Acknowledge.builder()
                .status(Acknowledge.Status.FAILURE).build());
    }

    @Test
    public void publishNext_noPost_exception() {
        when(postRepository.getNextToPublish()).thenReturn(Optional.empty());
        when(socialMediaPublisher1.ping()).thenReturn(Acknowledge.builder()
                .status(Acknowledge.Status.SUCCESS).build());
        when(socialMediaPublisher2.ping()).thenReturn(Acknowledge.builder()
                .status(Acknowledge.Status.SUCCESS).build());

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> postPublisher.publishNext());

        assertThat(exception.getMessage()).isEqualTo("There is not next post to publish");
    }

    @Test
    public void publishNext_publishFailed_exception() throws MalformedURLException {
        Post post = Post.builder()
                .id("1")
                .description("Post 1")
                .name("Post")
                .publishedDate(LocalDateTime.of(2020, 2, 3, 5, 6, 8, 1))
                .tags(List.of("tag1", "tag2"))
                .url(URI.create("https://myblog/post").toURL())
                .build();

        Post expectedPost = Post.builder()
                .id("1")
                .description("Post 1")
                .name("Post")
                .publishedDate(LocalDateTime.of(2020, 2, 3, 5, 6, 8, 1))
                .tags(List.of("tag1", "tag2"))
                .url(URI.create("https://myblog/post").toURL())
                .publications(List
                        .of(Publication.builder()
                                        .id("id")
                                        .publishedDate(LocalDateTime.of(2020, 3, 3, 5, 6, 8, 1))
                                        .status(Publication.Status.SUCCESS)
                                        .publisher("TWITTER")
                                        .build(),
                                Publication.builder()
                                        .publishedDate(LocalDateTime.of(2020, 3, 3, 5, 6, 8, 1))
                                        .status(Publication.Status.FAILURE)
                                        .publisher("LINKEDIN")
                                        .build()))
                .build();

        when(postRepository.getNextToPublish()).thenReturn(Optional.of(post));
        when(socialMediaPublisher1.ping()).thenReturn(Acknowledge.builder()
                .status(Acknowledge.Status.SUCCESS).build());
        when(socialMediaPublisher1.publish(post)).thenReturn(Publication.builder()
                .id("id")
                .publishedDate(LocalDateTime.of(2020, 3, 3, 5, 6, 8, 1))
                .status(Publication.Status.SUCCESS)
                .publisher("TWITTER")
                .build());
        when(socialMediaPublisher2.ping()).thenReturn(Acknowledge.builder()
                .status(Acknowledge.Status.SUCCESS).build());
        when(socialMediaPublisher2.publish(post)).thenReturn(Publication.builder()
                .publishedDate(LocalDateTime.of(2020, 3, 3, 5, 6, 8, 1))
                .status(Publication.Status.FAILURE)
                .publisher("LINKEDIN")
                .build());

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> postPublisher.publishNext());

        assertThat(exception.getMessage()).isEqualTo("Error publishing the post: " + expectedPost);
    }

    @Test
    public void publishNext_publishSuccess_publications() throws MalformedURLException {
        Post post = Post.builder()
                .id("1")
                .description("Post 1")
                .name("Post")
                .publishedDate(LocalDateTime.of(2020, 2, 3, 5, 6, 8, 1))
                .tags(List.of("tag1", "tag2"))
                .url(URI.create("https://myblog/post").toURL())
                .build();

        Post postUpdated = Post.builder()
                .id("1")
                .description("Post 1")
                .name("Post")
                .publishedDate(LocalDateTime.of(2020, 3, 3, 5, 6, 8, 1))
                .tags(List.of("tag1", "tag2"))
                .url(URI.create("https://myblog/post").toURL())
                .build();

        Post expectedPost = Post.builder()
                .id("1")
                .description("Post 1")
                .name("Post")
                .publishedDate(LocalDateTime.of(2020, 3, 3, 5, 6, 8, 1))
                .tags(List.of("tag1", "tag2"))
                .url(URI.create("https://myblog/post").toURL())
                .publications(List
                        .of(Publication.builder()
                                        .id("id")
                                        .publishedDate(LocalDateTime.of(2020, 3, 3, 5, 6, 8, 1))
                                        .status(Publication.Status.SUCCESS)
                                        .publisher("TWITTER")
                                        .build(),
                                Publication.builder()
                                        .id("id")
                                        .publishedDate(LocalDateTime.of(2020, 3, 3, 5, 6, 8, 1))
                                        .status(Publication.Status.SUCCESS)
                                        .publisher("LINKEDIN")
                                        .build()))
                .build();

        when(postRepository.getNextToPublish()).thenReturn(Optional.of(post));
        when(socialMediaPublisher1.ping()).thenReturn(Acknowledge.builder()
                .status(Acknowledge.Status.SUCCESS).build());
        when(socialMediaPublisher1.publish(post)).thenReturn(Publication.builder()
                .id("id")
                .publishedDate(LocalDateTime.of(2020, 3, 3, 5, 6, 8, 1))
                .status(Publication.Status.SUCCESS)
                .publisher("TWITTER")
                .build());
        when(socialMediaPublisher2.ping()).thenReturn(Acknowledge.builder()
                .status(Acknowledge.Status.SUCCESS).build());
        when(socialMediaPublisher2.publish(post)).thenReturn(Publication.builder()
                .id("id")
                .publishedDate(LocalDateTime.of(2020, 3, 3, 5, 6, 8, 1))
                .status(Publication.Status.SUCCESS)
                .publisher("LINKEDIN")
                .build());
        when(postRepository.update(postUpdated)).thenReturn(postUpdated);

        Post publishedPost = postPublisher.publishNext();

        assertThat(publishedPost).isEqualTo(expectedPost);
    }

}