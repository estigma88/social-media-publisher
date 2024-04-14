package com.coderstower.socialmediapubisher.main.socialmedia.linkedin;

import com.coderstower.socialmediapubisher.abstraction.post.repository.Post;
import com.coderstower.socialmediapubisher.abstraction.post.socialmedia.Acknowledge;
import com.coderstower.socialmediapubisher.abstraction.post.socialmedia.SocialMediaPublisher;
import com.coderstower.socialmediapubisher.abstraction.security.repository.OAuth2CredentialsRepository;
import com.coderstower.socialmediapubisher.abstraction.post.socialmedia.Publication;
import com.coderstower.socialmediapubisher.abstraction.security.UnauthorizedException;
import com.coderstower.socialmediapubisher.abstraction.security.repository.OAuth2Credentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        List<OAuth2Credentials> credentials = oauth2CredentialsRepository.findAll();

        if (credentials.isEmpty()) {
            throw new IllegalArgumentException("The credentials for " + name + " doesn't exist");
        }

        for (OAuth2Credentials credential : credentials) {
            if (areCredentialsExpired(credential)) {
                throw new UnauthorizedException("Unauthorized for " + name + " " + credential.getId() + ". Please login again here: " + loginURL.expand(name));
            }
        }

        return Acknowledge.builder()
                .status(Acknowledge.Status.SUCCESS)
                .build();
    }

    @Override
    public List<Publication> publish(Post post) {
        List<OAuth2Credentials> credentials = oauth2CredentialsRepository.findAll()
                .stream()
                .filter(oAuth2Credentials -> oAuth2Credentials.getAllowedGroups().contains(post.getGroup()))
                .collect(Collectors.toList());

        List<Publication> publications = new ArrayList<>();

        for (OAuth2Credentials credential : credentials) {
            try {
                Profile profile = getProfile(credential);

                LinkedInShare linkedInShare = LinkedInShare.builder()
                        .author("urn:li:person:" + profile.getSub())
                        .commentary(post.basicFormatWithoutURL())
                        .distribution(Distribution.builder().feedDistribution("MAIN_FEED").build())
                        .lifecycleState("PUBLISHED")
                        .content(Content.builder()
                                .article(ArticleContent.builder()
                                        .description(post.getDescription())
                                        .title(post.getName())
                                        .source(post.getUrl().toString())
                                        .build())
                                .build())
                        .visibility("PUBLIC")
                        .build();

                String shareId = publish(linkedInShare, credential);

                if (Objects.nonNull(shareId)) {
                    publications.add(Publication.builder()
                            .id(shareId)
                            .status(Publication.Status.SUCCESS)
                            .publisher(name)
                            .publishedDate(LocalDateTime.now(clock))
                            .credentialId(credential.getId())
                            .build());
                } else {
                    publications.add(Publication.builder()
                            .status(Publication.Status.FAILURE)
                            .publisher(name)
                            .publishedDate(LocalDateTime.now(clock))
                            .credentialId(credential.getId())
                            .build());
                }
            } catch (HttpClientErrorException e) {
                log.error("Error publishing to " + name + ", credentials " + credential.getId() + " response body: " + e.getResponseBodyAsString(), e);

                publications.add(Publication.builder()
                        .status(Publication.Status.FAILURE)
                        .publisher(name)
                        .publishedDate(LocalDateTime.now(clock))
                        .credentialId(credential.getId())
                        .build());

            } catch (Exception e) {
                log.error("Error publishing to " + name + ", credentials " + credential.getId(), e);

                publications.add(Publication.builder()
                        .status(Publication.Status.FAILURE)
                        .publisher(name)
                        .publishedDate(LocalDateTime.now(clock))
                        .credentialId(credential.getId())
                        .build());
            }
        }
        return publications;
    }

    private String publish(LinkedInShare linkedInShare, OAuth2Credentials credentials) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("X-Restli-Protocol-Version", "2.0.0");
        httpHeaders.add("LinkedIn-Version", "202304");
        httpHeaders.setBearerAuth(credentials.getAccessToken());

        HttpEntity<LinkedInShare> requestEntity = new HttpEntity<>(linkedInShare, httpHeaders);

        ResponseEntity<Void> response = restTemplate.exchange("https://api.linkedin.com/rest/posts", HttpMethod.POST, requestEntity, Void.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Problem trying to share a linkedin post: " + response.getStatusCode());
        }

        return response.getHeaders().getFirst("X-RestLi-Id");
    }

    private Profile getProfile(OAuth2Credentials credentials) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("X-Restli-Protocol-Version", "2.0.0");
        httpHeaders.add("LinkedIn-Version", "202304");
        httpHeaders.setBearerAuth(credentials.getAccessToken());

        HttpEntity<Void> requestEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<Profile> response = restTemplate.exchange("https://api.linkedin.com/v2/userinfo", HttpMethod.GET, requestEntity, Profile.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Problem trying to get the profile: " + response.getStatusCode());
        }

        return response.getBody();
    }

    private boolean areCredentialsExpired(OAuth2Credentials credentials) {
        return credentials.getExpirationDate().isBefore(LocalDateTime.now(clock));
    }
}
