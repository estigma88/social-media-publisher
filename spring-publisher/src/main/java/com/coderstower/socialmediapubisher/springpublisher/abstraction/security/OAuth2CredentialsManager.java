package com.coderstower.socialmediapubisher.springpublisher.abstraction.security;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.security.repository.OAuth2Credentials;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.security.repository.OAuth2CredentialsRepository;
import com.google.common.eventbus.EventBus;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Optional;

public class OAuth2CredentialsManager {
    private final OAuth2CredentialsRepository oAuth2CredentialsRepository;
    private final Map<String, String> principalNamesAllowed;
    private final EventBus eventBus;

    public OAuth2CredentialsManager(OAuth2CredentialsRepository oAuth2CredentialsRepository, Map<String, String> principalNamesAllowed, EventBus eventBus) {
        this.oAuth2CredentialsRepository = oAuth2CredentialsRepository;
        this.principalNamesAllowed = principalNamesAllowed;
        this.eventBus = eventBus;
    }

    public OAuth2Credentials update(OAuth2AuthorizedClient authorizedClient, String socialAccount) {
        if (isNotAllowed(authorizedClient, socialAccount)) {
            throw new UnauthorizedException();
        }

        OAuth2Credentials credentials = oAuth2CredentialsRepository.getCredentials(socialAccount)
                .orElseGet(() -> oAuth2CredentialsRepository.save(OAuth2Credentials.builder()
                        .id(socialAccount)
                        .build()));

        OAuth2Credentials updatedCredentials = credentials.update(
                authorizedClient.getAccessToken().getTokenValue(),
                LocalDateTime.ofInstant(
                        authorizedClient.getAccessToken().getExpiresAt(),
                        ZoneOffset.UTC)
        );

        OAuth2Credentials newCredentials = oAuth2CredentialsRepository.update(updatedCredentials);

//        eventBus.post(OAuth2CredentialsUpdated.builder()
//                .oAuth2Credentials(newCredentials)
//                .build());

        return newCredentials;
    }

    private boolean isNotAllowed(OAuth2AuthorizedClient authorizedClient, String socialAccount) {
        return Optional.ofNullable(authorizedClient)
                .map(OAuth2AuthorizedClient::getPrincipalName)
                .filter(principalName -> principalName.equals(principalNamesAllowed.get(socialAccount)))
                .isEmpty();
    }
}
