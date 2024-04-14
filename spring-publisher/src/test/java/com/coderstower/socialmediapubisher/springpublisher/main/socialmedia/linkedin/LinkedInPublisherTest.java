package com.coderstower.socialmediapubisher.springpublisher.main.socialmedia.linkedin;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository.Post;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.Acknowledge;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.Publication;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.security.UnauthorizedException;
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
import org.springframework.web.util.UriTemplate;

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
                .fixed(now.toInstant(), ZoneId.of("UTC")), new UriTemplate("http://localhost:8080/oauth2/{social-media}/credentials"));
    }

    @Test
    public void ping_noCredential_exception() {
        when(oauth2CredentialsRepository.findAll()).thenReturn(List.of());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> linkedInPublisher.ping());

        assertThat(exception.getMessage()).isEqualTo("The credentials for linkedin doesn't exist");
    }

    @Test
    public void ping_expiredCredential_exception() {
        when(oauth2CredentialsRepository.findAll()).thenReturn(List.of(OAuth2Credentials.builder()
                .expirationDate(now.toLocalDateTime().minusMonths(1))
                .id("credential1")
                .build()));

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> linkedInPublisher.ping());

        assertThat(exception.getMessage()).isEqualTo("Unauthorized for linkedin credential1. Please login again here: http://localhost:8080/oauth2/linkedin/credentials");
    }

    @Test
    public void ping_goodCredential_success() {
        when(oauth2CredentialsRepository.findAll()).thenReturn(List.of(OAuth2Credentials.builder()
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

        when(oauth2CredentialsRepository.findAll()).thenReturn(List.of(OAuth2Credentials.builder()
                .accessToken("accessToken")
                .allowedGroups(List.of("group1"))
                .build()));
        when(restTemplate.exchange("https://api.linkedin.com/v2/me", HttpMethod.GET, requestEntityProfile, Profile.class))
                .thenReturn(ResponseEntity.notFound().build());

        List<Publication> publication = linkedInPublisher.publish(Post.builder()
                .group("group1")
                .build());

        assertThat(publication).isEqualTo(List.of(Publication.builder()
                .status(Publication.Status.FAILURE)
                .publisher("linkedin")
                .publishedDate(now.toLocalDateTime())
                .build()));
    }

    @Test
    public void publish_noShare_publicationError() throws MalformedURLException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("X-Restli-Protocol-Version", "2.0.0");
        httpHeaders.setBearerAuth("accessToken");

        when(oauth2CredentialsRepository.findAll()).thenReturn(List.of(OAuth2Credentials.builder()
                .accessToken("accessToken")
                .allowedGroups(List.of("group1"))
                .build()));

        HttpEntity<Void> requestEntityProfile = new HttpEntity<>(httpHeaders);

        when(restTemplate.exchange("https://api.linkedin.com/v2/me", HttpMethod.GET, requestEntityProfile, Profile.class))
                .thenReturn(ResponseEntity.ok(Profile.builder()
                        .sub("memberid")
                        .build()));

        LinkedInShare linkedInShare = LinkedInShare.builder()
                .author("urn:li:person:memberid")
                .lifecycleState("PUBLISHED")
                .commentary("commentary")
                .distribution(Distribution.builder().feedDistribution("MAIN_FEED").build())
                .lifecycleState("PUBLISHED")
                .content(Content.builder()
                        .article(ArticleContent.builder()
                                .description("My second post\n\n#tag1 #tag2")
                                .title("My second post")
                                .source("https://coderstower.com/2020/01/13/open-close-principle-by-example/")
                                .build())
                        .build())
                .visibility("PUBLIC")
                .build();

        HttpEntity<LinkedInShare> requestShare = new HttpEntity<>(linkedInShare, httpHeaders);

        when(restTemplate.exchange("https://api.linkedin.com/v2/ugcPosts", HttpMethod.POST, requestShare, Void.class))
                .thenReturn(ResponseEntity.badRequest().build());

        List<Publication> publication = linkedInPublisher.publish(Post.builder()
                .id("2")
                .name("My Post 2")
                .description("My second post")
                .tags(List.of("tag1", "tag2"))
                .url(URI.create("https://coderstower.com/2020/01/13/open-close-principle-by-example/").toURL())
                .publishedDate(LocalDateTime.parse("2012-09-17T18:47:52"))
                .group("group1")
                .build());

        assertThat(publication).isEqualTo(List.of(Publication.builder()
                .status(Publication.Status.FAILURE)
                .publisher("linkedin")
                .publishedDate(now.toLocalDateTime())
                .build()));
    }

    @Test
    public void publish_noShareId_publicationError() throws MalformedURLException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("X-Restli-Protocol-Version", "2.0.0");
        httpHeaders.setBearerAuth("accessToken");

        when(oauth2CredentialsRepository.findAll()).thenReturn(List.of(OAuth2Credentials.builder()
                .accessToken("accessToken")
                .allowedGroups(List.of("group1"))
                .build()));

        HttpEntity<Void> requestEntityProfile = new HttpEntity<>(httpHeaders);

        when(restTemplate.exchange("https://api.linkedin.com/v2/me", HttpMethod.GET, requestEntityProfile, Profile.class))
                .thenReturn(ResponseEntity.ok(Profile.builder()
                        .sub("memberid")
                        .build()));

        LinkedInShare linkedInShare = LinkedInShare.builder()
                .author("urn:li:person:memberid")
                .lifecycleState("PUBLISHED")
                .commentary("commentary")
                .distribution(Distribution.builder().feedDistribution("MAIN_FEED").build())
                .lifecycleState("PUBLISHED")
                .content(Content.builder()
                        .article(ArticleContent.builder()
                                .description("My second post\n\n#tag1 #tag2")
                                .title("My second post")
                                .source("https://coderstower.com/2020/01/13/open-close-principle-by-example/")
                                .build())
                        .build())
                .visibility("PUBLIC")
                .build();

        HttpEntity<LinkedInShare> requestShare = new HttpEntity<>(linkedInShare, httpHeaders);

        when(restTemplate.exchange("https://api.linkedin.com/v2/ugcPosts", HttpMethod.POST, requestShare, Void.class))
                .thenReturn(ResponseEntity.ok().build());

        List<Publication> publication = linkedInPublisher.publish(Post.builder()
                .id("2")
                .name("My Post 2")
                .description("My second post")
                .tags(List.of("tag1", "tag2"))
                .url(URI.create("https://coderstower.com/2020/01/13/open-close-principle-by-example/").toURL())
                .publishedDate(LocalDateTime.parse("2012-09-17T18:47:52"))
                .group("group1")
                .build());

        assertThat(publication).isEqualTo(List.of(Publication.builder()
                .status(Publication.Status.FAILURE)
                .publisher("linkedin")
                .publishedDate(now.toLocalDateTime())
                .build()));
    }

    @Test
    public void publish_shareId_publicationSucced() throws MalformedURLException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("X-Restli-Protocol-Version", "2.0.0");
        httpHeaders.add("LinkedIn-Version", "202304");
        httpHeaders.setBearerAuth("accessToken");

        when(oauth2CredentialsRepository.findAll()).thenReturn(List.of(OAuth2Credentials.builder()
                .accessToken("accessToken")
                .allowedGroups(List.of("group1"))
                .build()));

        HttpEntity<Void> requestEntityProfile = new HttpEntity<>(httpHeaders);

        when(restTemplate.exchange("https://api.linkedin.com/v2/userinfo", HttpMethod.GET, requestEntityProfile, Profile.class))
                .thenReturn(ResponseEntity.ok(Profile.builder()
                        .sub("memberid")
                        .build()));

        LinkedInShare linkedInShare = LinkedInShare.builder()
                .author("urn:li:person:memberid")
                .lifecycleState("PUBLISHED")
                .commentary("My second post\n\n#tag1 #tag2")
                .distribution(Distribution.builder().feedDistribution("MAIN_FEED").build())
                .lifecycleState("PUBLISHED")
                .content(Content.builder()
                        .article(ArticleContent.builder()
                                .description("My second post")
                                .title("My Post 2")
                                .source("https://coderstower.com/2020/01/13/open-close-principle-by-example/")
                                .build())
                        .build())
                .visibility("PUBLIC")
                .build();

        HttpEntity<LinkedInShare> requestShare = new HttpEntity<>(linkedInShare, httpHeaders);

        when(restTemplate.exchange("https://api.linkedin.com/rest/posts", HttpMethod.POST, requestShare, Void.class))
                .thenReturn(ResponseEntity.ok().header("X-RestLi-Id", "shareId").build());

        List<Publication> publication = linkedInPublisher.publish(Post.builder()
                .id("2")
                .name("My Post 2")
                .description("My second post")
                .tags(List.of("tag1", "tag2"))
                .url(URI.create("https://coderstower.com/2020/01/13/open-close-principle-by-example/").toURL())
                .publishedDate(LocalDateTime.parse("2012-09-17T18:47:52"))
                .group("group1")
                .build());

        assertThat(publication).isEqualTo(List.of(Publication.builder()
                .id("shareId")
                .status(Publication.Status.SUCCESS)
                .publisher("linkedin")
                .publishedDate(now.toLocalDateTime())
                .build()));
    }
}