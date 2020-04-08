package com.coderstower.socialmediapubisher.springpublisher.main.socialmedia.linkedin;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository.Post;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.Acknowledge;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.Publication;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.security.repository.OAuth2Credentials;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.security.repository.OAuth2CredentialsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

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
class LinkedInPublisherTest {
    @Mock
    private OAuth2CredentialsRepository oauth2CredentialsRepository;
    @Mock
    private RestTemplate restTemplate;

    private LinkedInPublisher linkedInPublisher;
    private ZonedDateTime now;

    @BeforeEach
    public void before() {
        this.now = ZonedDateTime.of(2020, 3, 3, 5, 6, 8, 1, ZoneId.of("UTC"));
        this.linkedInPublisher = new LinkedInPublisher("linkedin", oauth2CredentialsRepository, restTemplate, Clock
                .fixed(now.toInstant(), ZoneId.of("UTC")));
    }

    @Test
    public void ping_noCredential_exception() {
        when(oauth2CredentialsRepository.getCredentials("linkedin")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> linkedInPublisher.ping());

        assertThat(exception.getMessage()).isEqualTo("The credentials for linkedin doesn't exist");
    }

    @Test
    public void ping_expiredCredential_exception() {
        when(oauth2CredentialsRepository.getCredentials("linkedin")).thenReturn(Optional.of(OAuth2Credentials.builder()
                .expirationDate(now.toLocalDateTime().minusMonths(1))
                .build()));

        Acknowledge ack = linkedInPublisher.ping();

        assertThat(ack).isEqualTo(Acknowledge.builder()
                .status(Acknowledge.Status.FAILURE)
                .description("Credentials expired")
                .build());
    }

    @Test
    public void ping_goodCredential_success() {
        when(oauth2CredentialsRepository.getCredentials("linkedin")).thenReturn(Optional.of(OAuth2Credentials.builder()
                .expirationDate(now.toLocalDateTime().plusMonths(1))
                .build()));

        Acknowledge ack = linkedInPublisher.ping();

        assertThat(ack).isEqualTo(Acknowledge.builder()
                .status(Acknowledge.Status.SUCCESS)
                .build());
    }

    @Test
    public void publish_noProfile_publicationError() {
        HttpHeaders httpHeadersProfile = new HttpHeaders();
        httpHeadersProfile.setContentType(MediaType.APPLICATION_JSON);
        httpHeadersProfile.add("X-Restli-Protocol-Version", "2.0.0");
        httpHeadersProfile.setBearerAuth("accessToken");

        HttpEntity<Void> requestEntityProfile = new HttpEntity<>(httpHeadersProfile);

        when(oauth2CredentialsRepository.getCredentials("linkedin")).thenReturn(Optional.of(OAuth2Credentials.builder()
                .accessToken("accessToken")
                .build()));
        when(restTemplate.exchange("https://api.linkedin.com/v2/me", HttpMethod.GET, requestEntityProfile, Profile.class))
                .thenReturn(ResponseEntity.notFound().build());

        Publication publication = linkedInPublisher.publish(Post.builder().build());

        assertThat(publication).isEqualTo(Publication.builder()
                .status(Publication.Status.FAILURE)
                .publisher("linkedin")
                .publishedDate(now.toLocalDateTime())
                .build());
    }

    @Test
    public void publish_noShare_publicationError() throws MalformedURLException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("X-Restli-Protocol-Version", "2.0.0");
        httpHeaders.setBearerAuth("accessToken");

        when(oauth2CredentialsRepository.getCredentials("linkedin")).thenReturn(Optional.of(OAuth2Credentials.builder()
                .accessToken("accessToken")
                .build()));

        HttpEntity<Void> requestEntityProfile = new HttpEntity<>(httpHeaders);

        when(restTemplate.exchange("https://api.linkedin.com/v2/me", HttpMethod.GET, requestEntityProfile, Profile.class))
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
                .thenReturn(ResponseEntity.badRequest().build());

        Publication publication = linkedInPublisher.publish(Post.builder()
                .id("2")
                .name("My Post 2")
                .description("My second post")
                .tags(List.of("tag1", "tag2"))
                .url(URI.create("https://coderstower.com/2020/01/13/open-close-principle-by-example/").toURL())
                .publishedDate(LocalDateTime.parse("2012-09-17T18:47:52"))
                .build());

        assertThat(publication).isEqualTo(Publication.builder()
                .status(Publication.Status.FAILURE)
                .publisher("linkedin")
                .publishedDate(now.toLocalDateTime())
                .build());
    }

    @Test
    public void publish_noShareId_publicationError() throws MalformedURLException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("X-Restli-Protocol-Version", "2.0.0");
        httpHeaders.setBearerAuth("accessToken");

        when(oauth2CredentialsRepository.getCredentials("linkedin")).thenReturn(Optional.of(OAuth2Credentials.builder()
                .accessToken("accessToken")
                .build()));

        HttpEntity<Void> requestEntityProfile = new HttpEntity<>(httpHeaders);

        when(restTemplate.exchange("https://api.linkedin.com/v2/me", HttpMethod.GET, requestEntityProfile, Profile.class))
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
                .thenReturn(ResponseEntity.ok().build());

        Publication publication = linkedInPublisher.publish(Post.builder()
                .id("2")
                .name("My Post 2")
                .description("My second post")
                .tags(List.of("tag1", "tag2"))
                .url(URI.create("https://coderstower.com/2020/01/13/open-close-principle-by-example/").toURL())
                .publishedDate(LocalDateTime.parse("2012-09-17T18:47:52"))
                .build());

        assertThat(publication).isEqualTo(Publication.builder()
                .status(Publication.Status.FAILURE)
                .publisher("linkedin")
                .publishedDate(now.toLocalDateTime())
                .build());
    }

    @Test
    public void publish_shareId_publicationSucced() throws MalformedURLException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("X-Restli-Protocol-Version", "2.0.0");
        httpHeaders.setBearerAuth("accessToken");

        when(oauth2CredentialsRepository.getCredentials("linkedin")).thenReturn(Optional.of(OAuth2Credentials.builder()
                .accessToken("accessToken")
                .build()));

        HttpEntity<Void> requestEntityProfile = new HttpEntity<>(httpHeaders);

        when(restTemplate.exchange("https://api.linkedin.com/v2/me", HttpMethod.GET, requestEntityProfile, Profile.class))
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
                .thenReturn(ResponseEntity.ok().header("X-RestLi-Id", "shareId").build());

        Publication publication = linkedInPublisher.publish(Post.builder()
                .id("2")
                .name("My Post 2")
                .description("My second post")
                .tags(List.of("tag1", "tag2"))
                .url(URI.create("https://coderstower.com/2020/01/13/open-close-principle-by-example/").toURL())
                .publishedDate(LocalDateTime.parse("2012-09-17T18:47:52"))
                .build());

        assertThat(publication).isEqualTo(Publication.builder()
                .id("shareId")
                .status(Publication.Status.SUCCESS)
                .publisher("linkedin")
                .publishedDate(now.toLocalDateTime())
                .build());
    }
}