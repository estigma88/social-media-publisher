package com.coderstower.socialmediapubisher.springpublisher.main.socialmedia.twitter;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository.Post;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.Acknowledge;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.Publication;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.repository.credential.Oauth1Credentials;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.repository.credential.Oauth1CredentialsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.Authorization;
import twitter4j.auth.NullAuthorization;

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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TwitterPublisherTest {
    @Mock
    private Oauth1CredentialsRepository oauth1CredentialsRepository;
    @Mock
    private Twitter twitter;
    private TwitterPublisher twitterPublisher;
    private ZonedDateTime now;

    @BeforeEach
    public void before() {
        this.now = ZonedDateTime.of(2020, 3, 3, 5, 6, 8, 1, ZoneId.of("UTC"));
        this.twitterPublisher = new TwitterPublisher("twitter", oauth1CredentialsRepository, twitter, Clock
                .fixed(now.toInstant(), ZoneId.of("UTC")));
    }

    @Test
    public void ping_noCredential_exception() {
        when(oauth1CredentialsRepository.getCredentials("twitter")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> twitterPublisher.ping());

        assertThat(exception.getMessage()).isEqualTo("The credentials for twitter doesn't exist");
    }

    @Test
    public void ping_credentialNoSetTwitterError_ackFailure() throws TwitterException {
        when(oauth1CredentialsRepository.getCredentials("twitter")).thenReturn(Optional.of(Oauth1Credentials.builder()
                .accessToken("accessToken")
                .consumerKey("consumerKey")
                .consumerSecret("consumerSecret")
                .tokenSecret("tokenSecret")
                .build()));
        when(twitter.getAuthorization()).thenReturn(NullAuthorization.getInstance());
        when(twitter.getHomeTimeline(new Paging(1, 1))).thenThrow(new TwitterException("error"));

        Acknowledge ack = twitterPublisher.ping();

        assertThat(ack).isEqualTo(Acknowledge.builder()
                .status(Acknowledge.Status.FAILURE)
                .description("Ping error")
                .exception(new TwitterException("error"))
                .build());
        verify(twitter).setOAuthConsumer("consumerKey", "consumerSecret");
        verify(twitter).setOAuthAccessToken(new AccessToken("accessToken", "tokenSecret"));
    }

    @Test
    public void ping_credentialSetEmptyStatus_ackFailure() throws TwitterException {
        when(oauth1CredentialsRepository.getCredentials("twitter")).thenReturn(Optional.of(Oauth1Credentials.builder()
                .accessToken("accessToken")
                .consumerKey("consumerKey")
                .consumerSecret("consumerSecret")
                .tokenSecret("tokenSecret")
                .build()));
        when(twitter.getAuthorization()).thenReturn(mock(Authorization.class));
        when(twitter.getHomeTimeline(new Paging(1, 1))).thenReturn(null);

        Acknowledge ack = twitterPublisher.ping();

        assertThat(ack).isEqualTo(Acknowledge.builder()
                .status(Acknowledge.Status.FAILURE)
                .build());
        verify(twitter, times(0)).setOAuthConsumer("consumerKey", "consumerSecret");
        verify(twitter, times(0)).setOAuthAccessToken(new AccessToken("accessToken", "tokenSecret"));
    }

    @Test
    public void ping_credentialSetStatus_ackSucced() throws TwitterException {
        when(oauth1CredentialsRepository.getCredentials("twitter")).thenReturn(Optional.of(Oauth1Credentials.builder()
                .accessToken("accessToken")
                .consumerKey("consumerKey")
                .consumerSecret("consumerSecret")
                .tokenSecret("tokenSecret")
                .build()));
        when(twitter.getAuthorization()).thenReturn(mock(Authorization.class));
        when(twitter.getHomeTimeline(new Paging(1, 1))).thenReturn(mock(ResponseList.class));

        Acknowledge ack = twitterPublisher.ping();

        assertThat(ack).isEqualTo(Acknowledge.builder()
                .status(Acknowledge.Status.SUCCESS)
                .build());
        verify(twitter, times(0)).setOAuthConsumer("consumerKey", "consumerSecret");
        verify(twitter, times(0)).setOAuthAccessToken(new AccessToken("accessToken", "tokenSecret"));
    }

    @Test
    public void publish_noCredential_exception() {
        when(oauth1CredentialsRepository.getCredentials("twitter")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> twitterPublisher.publish(Post.builder().build()));

        assertThat(exception.getMessage()).isEqualTo("The credentials for twitter doesn't exist");
    }

    @Test
    public void publish_credentialNoSetTwitterError_shareFailure() throws TwitterException, MalformedURLException {
        when(oauth1CredentialsRepository.getCredentials("twitter")).thenReturn(Optional.of(Oauth1Credentials.builder()
                .accessToken("accessToken")
                .consumerKey("consumerKey")
                .consumerSecret("consumerSecret")
                .tokenSecret("tokenSecret")
                .build()));
        when(twitter.getAuthorization()).thenReturn(NullAuthorization.getInstance());
        when(twitter.updateStatus("My second post\n\n#tag1 #tag2\n\nhttps://coderstower.com/2020/01/13/open-close-principle-by-example/")).thenThrow(new TwitterException("error"));

        Publication publish = twitterPublisher.publish(Post.builder()
                .id("2")
                .name("My Post 2")
                .description("My second post")
                .tags(List.of("tag1", "tag2"))
                .url(URI.create("https://coderstower.com/2020/01/13/open-close-principle-by-example/").toURL())
                .publishedDate(LocalDateTime.parse("2012-09-17T18:47:52"))
                .build());

        assertThat(publish).isEqualTo(Publication.builder()
                .status(Publication.Status.FAILURE)
                .publisher("twitter")
                .publishedDate(now.toLocalDateTime())
                .build());
        verify(twitter).setOAuthConsumer("consumerKey", "consumerSecret");
        verify(twitter).setOAuthAccessToken(new AccessToken("accessToken", "tokenSecret"));
    }

    @Test
    public void publish_credentialSetEmptyStatus_shareFailure() throws TwitterException, MalformedURLException {
        when(oauth1CredentialsRepository.getCredentials("twitter")).thenReturn(Optional.of(Oauth1Credentials.builder()
                .accessToken("accessToken")
                .consumerKey("consumerKey")
                .consumerSecret("consumerSecret")
                .tokenSecret("tokenSecret")
                .build()));
        when(twitter.getAuthorization()).thenReturn(mock(Authorization.class));
        when(twitter.updateStatus("My second post\n\n#tag1 #tag2\n\nhttps://coderstower.com/2020/01/13/open-close-principle-by-example/")).thenReturn(null);

        Publication publish = twitterPublisher.publish(Post.builder()
                .id("2")
                .name("My Post 2")
                .description("My second post")
                .tags(List.of("tag1", "tag2"))
                .url(URI.create("https://coderstower.com/2020/01/13/open-close-principle-by-example/").toURL())
                .publishedDate(LocalDateTime.parse("2012-09-17T18:47:52"))
                .build());

        assertThat(publish).isEqualTo(Publication.builder()
                .status(Publication.Status.FAILURE)
                .publisher("twitter")
                .publishedDate(now.toLocalDateTime())
                .build());
        verify(twitter, times(0)).setOAuthConsumer("consumerKey", "consumerSecret");
        verify(twitter, times(0)).setOAuthAccessToken(new AccessToken("accessToken", "tokenSecret"));
    }

    @Test
    public void publish_credentialSetStatus_ackSucced() throws TwitterException, MalformedURLException {
        when(oauth1CredentialsRepository.getCredentials("twitter")).thenReturn(Optional.of(Oauth1Credentials.builder()
                .accessToken("accessToken")
                .consumerKey("consumerKey")
                .consumerSecret("consumerSecret")
                .tokenSecret("tokenSecret")
                .build()));
        when(twitter.getAuthorization()).thenReturn(mock(Authorization.class));

        Status status = mock(Status.class);
        when(status.getId()).thenReturn(123L);
        when(twitter.updateStatus("My second post\n\n#tag1 #tag2\n\nhttps://coderstower.com/2020/01/13/open-close-principle-by-example/")).thenReturn(status);

        Publication publish = twitterPublisher.publish(Post.builder()
                .id("2")
                .name("My Post 2")
                .description("My second post")
                .tags(List.of("tag1", "tag2"))
                .url(URI.create("https://coderstower.com/2020/01/13/open-close-principle-by-example/").toURL())
                .publishedDate(LocalDateTime.parse("2012-09-17T18:47:52"))
                .build());

        assertThat(publish).isEqualTo(Publication.builder()
                .id("123")
                .status(Publication.Status.SUCCESS)
                .publisher("twitter")
                .publishedDate(now.toLocalDateTime())
                .build());
        verify(twitter, times(0)).setOAuthConsumer("consumerKey", "consumerSecret");
        verify(twitter, times(0)).setOAuthAccessToken(new AccessToken("accessToken", "tokenSecret"));
    }
}