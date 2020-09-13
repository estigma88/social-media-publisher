package com.coderstower.socialmediapubisher.springpublisher.main.socialmedia.linkedin;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository.Post;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.Acknowledge;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.Publication;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.SocialMediaPublisher;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.security.UnauthorizedException;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.security.repository.OAuth2Credentials;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.security.repository.OAuth2CredentialsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
public class LinkedInPublisher implements SocialMediaPublisher {
    private final String name;
    private final OAuth2CredentialsRepository oauth2CredentialsRepository;
    private final RestTemplate restTemplate;
    private final Clock clock;
    private final UriTemplate loginURL;

    public LinkedInPublisher(String name, OAuth2CredentialsRepository oauth2CredentialsRepository, RestTemplate restTemplate, Clock clock, UriTemplate loginURL) {
        this.name = name;
        this.oauth2CredentialsRepository = oauth2CredentialsRepository;
        this.restTemplate = restTemplate;
        this.clock = clock;
        this.loginURL = loginURL;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Acknowledge ping() {
        OAuth2Credentials credentials = oauth2CredentialsRepository.getCredentials(name)
                .orElseThrow(() -> new IllegalArgumentException("The credentials for " + name + " doesn't exist"));

        if (areCredentialsExpired(credentials)) {
            throw new UnauthorizedException("Unauthorized. Please login again here: " + loginURL.expand(name));
        } else {
            return Acknowledge.builder()
                    .status(Acknowledge.Status.SUCCESS)
                    .build();
        }
    }

    @Override
    public Publication publish(Post post) {
        OAuth2Credentials credentials = oauth2CredentialsRepository.getCredentials(name)
                .orElseThrow(() -> new IllegalArgumentException("The credentials for " + name + " doesn't exist"));

        try {
            Profile profile = getProfile(credentials);

            LinkedInShare linkedInShare = LinkedInShare.builder()
                    .author("urn:li:person:" + profile.getId())
                    .lifecycleState("PUBLISHED")
                    .specificContent(SpecificContent.builder()
                            .shareContent(ShareContent.builder()
                                    .shareCommentary(Text.builder()
                                            .text(post.basicFormatWithoutURL())
                                            .build())
                                    .shareMediaCategory("ARTICLE")
                                    .media(Media.builder()
                                            .description(Text.builder()
                                                    .text(post.getDescription())
                                                    .build())
                                            .title(Text.builder()
                                                    .text(post.getName())
                                                    .build())
                                            .status("READY")
                                            .originalUrl(post.getUrl().toString())
                                            .build())
                                    .build())
                            .build())
                    .visibility(Visibility.builder()
                            .memberNetworkVisibility("PUBLIC")
                            .build())
                    .build();

            String shareId = publish(linkedInShare, credentials);

            if (Objects.nonNull(shareId)) {
                return Publication.builder()
                        .id(shareId)
                        .status(Publication.Status.SUCCESS)
                        .publisher(name)
                        .publishedDate(LocalDateTime.now(clock))
                        .build();
            } else {
                return Publication.builder()
                        .status(Publication.Status.FAILURE)
                        .publisher(name)
                        .publishedDate(LocalDateTime.now(clock))
                        .build();
            }
        } catch (Exception e) {
            log.error("Error publishing to " + name, e);

            return Publication.builder()
                    .status(Publication.Status.FAILURE)
                    .publisher(name)
                    .publishedDate(LocalDateTime.now(clock))
                    .build();
        }
    }

    private String publish(LinkedInShare linkedInShare, OAuth2Credentials credentials) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("X-Restli-Protocol-Version", "2.0.0");
        httpHeaders.setBearerAuth(credentials.getAccessToken());

        HttpEntity<LinkedInShare> requestEntity = new HttpEntity<>(linkedInShare, httpHeaders);

        ResponseEntity<Void> response = restTemplate.exchange("https://api.linkedin.com/v2/ugcPosts", HttpMethod.POST, requestEntity, Void.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Problem trying to share a linkedin post: " + response.getStatusCode());
        }

        return response.getHeaders().getFirst("X-RestLi-Id");
    }

    private Profile getProfile(OAuth2Credentials credentials) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("X-Restli-Protocol-Version", "2.0.0");
        httpHeaders.setBearerAuth(credentials.getAccessToken());

        HttpEntity<Void> requestEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<Profile> response = restTemplate.exchange("https://api.linkedin.com/v2/me", HttpMethod.GET, requestEntity, Profile.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Problem trying to get the profile: " + response.getStatusCode());
        }

        return response.getBody();
    }

    private boolean areCredentialsExpired(OAuth2Credentials credentials) {
        return credentials.getExpirationDate().isBefore(LocalDateTime.now(clock));
    }
}
