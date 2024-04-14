package com.coderstower.socialmediapubisher.application.aws.repository.oauth1;

import com.coderstower.socialmediapubisher.domain.security.repository.OAuth1Credentials;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OAuth1CredentialAWSRepositoryTest {
    @Mock
    private OAuth1CredentialDynamoRepository oauth1CredentialDynamoRepository;
    @InjectMocks
    private OAuth1CredentialAWSRepository oauth1CredentialAWSRepository;

    @Test
    public void getCredentials(){
        when(oauth1CredentialDynamoRepository.findById("twitter")).thenReturn(Optional.of(OAuth1CredentialDynamo.builder()
                .id("twitter")
                .accessToken("accessToken")
                .tokenSecret("tokenSecret")
                .consumerKey("consumerKey")
                .consumerSecret("consumerSecret")
                .build()));

        Optional<OAuth1Credentials> credentials = oauth1CredentialAWSRepository.getCredentials("twitter");

        assertThat(credentials).isEqualTo(Optional.of(OAuth1Credentials.builder()
                .id("twitter")
                .accessToken("accessToken")
                .tokenSecret("tokenSecret")
                .consumerKey("consumerKey")
                .consumerSecret("consumerSecret")
                .build()));
    }

}