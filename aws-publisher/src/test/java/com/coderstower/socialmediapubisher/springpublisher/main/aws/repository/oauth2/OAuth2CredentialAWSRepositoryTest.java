package com.coderstower.socialmediapubisher.springpublisher.main.aws.repository.oauth2;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.security.repository.OAuth2Credentials;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OAuth2CredentialAWSRepositoryTest {
    @Mock
    private OAuth2CredentialDynamoRepository oauth2CredentialDynamoRepository;
    @InjectMocks
    private OAuth2CredentialAWSRepository oauth2CredentialAWSRepository;

    @Test
    public void getCredentials(){
        when(oauth2CredentialDynamoRepository.findById("linkedin")).thenReturn(Optional.of(OAuth2CredentialDynamo.builder()
                .id("linkedin")
                .accessToken("accessToken")
                .expirationDate(LocalDateTime.of(2020, 3, 3, 5, 6, 8, 1))
                .build()));

        Optional<OAuth2Credentials> credentials = oauth2CredentialAWSRepository.getCredentials("linkedin");

        assertThat(credentials).isEqualTo(Optional.of(OAuth2Credentials.builder()
                .id("linkedin")
                .accessToken("accessToken")
                .expirationDate(LocalDateTime.of(2020, 3, 3, 5, 6, 8, 1))
                .build()));
    }

    @Test
    public void update(){
        when(oauth2CredentialDynamoRepository.save(OAuth2CredentialDynamo.builder()
                .id("linkedin")
                .accessToken("accessToken")
                .expirationDate(LocalDateTime.of(2020, 3, 3, 5, 6, 8, 1))
                .build())).thenReturn(OAuth2CredentialDynamo.builder()
                .id("linkedin")
                .accessToken("accessToken")
                .expirationDate(LocalDateTime.of(2020, 3, 3, 5, 6, 8, 1))
                .build());

        OAuth2Credentials credentials = oauth2CredentialAWSRepository.update(OAuth2Credentials.builder()
                .id("linkedin")
                .accessToken("accessToken")
                .expirationDate(LocalDateTime.of(2020, 3, 3, 5, 6, 8, 1))
                .build());

        assertThat(credentials).isEqualTo(OAuth2Credentials.builder()
                .id("linkedin")
                .accessToken("accessToken")
                .expirationDate(LocalDateTime.of(2020, 3, 3, 5, 6, 8, 1))
                .build());
    }

}