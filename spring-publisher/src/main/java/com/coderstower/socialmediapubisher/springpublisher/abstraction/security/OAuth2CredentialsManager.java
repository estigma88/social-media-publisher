package com.coderstower.socialmediapubisher.springpublisher.abstraction.security;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.security.repository.OAuth2Credentials;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.security.repository.OAuth2CredentialsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Optional;

public class OAuth2CredentialsManager {
    private final OAuth2CredentialsRepository oAuth2CredentialsRepository;
    private final Map<String, String> principalNamesAllowed;

    public OAuth2CredentialsManager(OAuth2CredentialsRepository oAuth2CredentialsRepository, Map<String, String> principalNamesAllowed) {
        this.oAuth2CredentialsRepository = oAuth2CredentialsRepository;
        this.principalNamesAllowed = principalNamesAllowed;
    }

    public OAuth2Credentials update(OAuth2AuthorizedClient authorizedClient, String socialAccount) {
        if (isNotAllowed(authorizedClient, socialAccount)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        OAuth2Credentials credentials = oAuth2CredentialsRepository.getCredentials(socialAccount)
                .orElseThrow(() -> new IllegalArgumentException("Social account doesn't exist"));

        OAuth2Credentials updatedCredentials = credentials.update(authorizedClient.getAccessToken().getTokenValue(),
                LocalDateTime.ofInstant(authorizedClient.getAccessToken().getExpiresAt(), ZoneOffset.UTC));

        return oAuth2CredentialsRepository.update(updatedCredentials);
    }

    private boolean isNotAllowed(OAuth2AuthorizedClient authorizedClient, String socialAccount) {
        return Optional.ofNullable(authorizedClient)
                .map(OAuth2AuthorizedClient::getPrincipalName)
                .filter(principalName -> principalName.equals(principalNamesAllowed.get(socialAccount)))
                .isEmpty();
    }
}
