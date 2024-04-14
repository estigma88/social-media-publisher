package com.coderstower.socialmediapubisher.domain.security;

import com.coderstower.socialmediapubisher.domain.security.repository.OAuth2Credentials;
import com.coderstower.socialmediapubisher.domain.security.repository.OAuth2CredentialsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OAuth2CredentialsManagerTest {
    @Mock
    private OAuth2CredentialsRepository oAuth2CredentialsRepository;
    @Mock
    private OAuth2AuthorizedClient oAuth2AuthorizedClient;

    private OAuth2CredentialsManager oAuth2CredentialsManager;

    @BeforeEach
    public void before() {
        oAuth2CredentialsManager = new OAuth2CredentialsManager(oAuth2CredentialsRepository, Map.of("linkedin", "myuser"));
    }

    @Test
    public void update_notAllowed_unauthorized() {
        when(oAuth2AuthorizedClient.getPrincipalName()).thenReturn("otheruser");

        assertThrows(UnauthorizedException.class, () -> oAuth2CredentialsManager.update(oAuth2AuthorizedClient, "linkedin"));
    }

    @Test
    public void update_credentialsNotFound_created() {
        when(oAuth2AuthorizedClient.getPrincipalName()).thenReturn("myuser");
        when(oAuth2CredentialsRepository.getCredentials("linkedin")).thenReturn(Optional.empty());
        when(oAuth2CredentialsRepository.save(OAuth2Credentials.builder()
                .id("linkedin")
                .build())).thenReturn(OAuth2Credentials.builder()
                .id("linkedin")
                .build());
        when(oAuth2AuthorizedClient.getAccessToken()).thenReturn(new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "newAccessToken",
                LocalDateTime.of(2020, 4, 3, 5, 6, 8, 1).toInstant(ZoneOffset.UTC),
                LocalDateTime.of(2020, 6, 3, 5, 6, 8, 1).toInstant(ZoneOffset.UTC)));
        when(oAuth2CredentialsRepository.update(OAuth2Credentials.builder()
                .id("linkedin")
                .accessToken("newAccessToken")
                .expirationDate(LocalDateTime.of(2020, 6, 3, 5, 6, 8, 1))
                .build())).thenReturn(OAuth2Credentials.builder()
                .id("linkedin")
                .accessToken("newAccessToken")
                .expirationDate(LocalDateTime.of(2020, 6, 3, 5, 6, 8, 1))
                .build());

        OAuth2Credentials credentials = oAuth2CredentialsManager.update(oAuth2AuthorizedClient, "linkedin");

        assertThat(credentials).isEqualTo(OAuth2Credentials.builder()
                .id("linkedin")
                .accessToken("newAccessToken")
                .expirationDate(LocalDateTime.of(2020, 6, 3, 5, 6, 8, 1))
                .build());
    }

    @Test
    public void update_credentialsFound_updated() {
        when(oAuth2AuthorizedClient.getPrincipalName()).thenReturn("myuser");
        when(oAuth2CredentialsRepository.getCredentials("linkedin")).thenReturn(Optional.of(OAuth2Credentials.builder()
                .id("linkedin")
                .accessToken("previousAccessToken")
                .expirationDate(LocalDateTime.of(2020, 2, 3, 5, 6, 8, 1))
                .build()));
        when(oAuth2AuthorizedClient.getAccessToken()).thenReturn(new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "newAccessToken",
                LocalDateTime.of(2020, 4, 3, 5, 6, 8, 1).toInstant(ZoneOffset.UTC),
                LocalDateTime.of(2020, 6, 3, 5, 6, 8, 1).toInstant(ZoneOffset.UTC)));
        when(oAuth2CredentialsRepository.update(OAuth2Credentials.builder()
                .id("linkedin")
                .accessToken("newAccessToken")
                .expirationDate(LocalDateTime.of(2020, 6, 3, 5, 6, 8, 1))
                .build())).thenReturn(OAuth2Credentials.builder()
                .id("linkedin")
                .accessToken("newAccessToken")
                .expirationDate(LocalDateTime.of(2020, 6, 3, 5, 6, 8, 1))
                .build());

        OAuth2Credentials credentials = oAuth2CredentialsManager.update(oAuth2AuthorizedClient, "linkedin");

        assertThat(credentials).isEqualTo(OAuth2Credentials.builder()
                .id("linkedin")
                .accessToken("newAccessToken")
                .expirationDate(LocalDateTime.of(2020, 6, 3, 5, 6, 8, 1))
                .build());
    }
}