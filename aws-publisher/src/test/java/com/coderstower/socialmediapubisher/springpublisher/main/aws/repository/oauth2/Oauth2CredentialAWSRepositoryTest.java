package com.coderstower.socialmediapubisher.springpublisher.main.aws.repository.oauth2;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.repository.credential.Oauth2Credentials;
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
class Oauth2CredentialAWSRepositoryTest {
    @Mock
    private Oauth2CredentialDynamoRepository oauth2CredentialDynamoRepository;
    @InjectMocks
    private Oauth2CredentialAWSRepository oauth2CredentialAWSRepository;

    @Test
    public void getCredentials(){
        when(oauth2CredentialDynamoRepository.findById("linkedin")).thenReturn(Optional.of(Oauth2CredentialDynamo.builder()
                .id("linkedin")
                .accessToken("accessToken")
                .expirationDate(LocalDateTime.of(2020, 3, 3, 5, 6, 8, 1))
                .build()));

        Optional<Oauth2Credentials> credentials = oauth2CredentialAWSRepository.getCredentials("linkedin");

        assertThat(credentials).isEqualTo(Optional.of(Oauth2Credentials.builder()
                .id("linkedin")
                .accessToken("accessToken")
                .expirationDate(LocalDateTime.of(2020, 3, 3, 5, 6, 8, 1))
                .build()));
    }

}