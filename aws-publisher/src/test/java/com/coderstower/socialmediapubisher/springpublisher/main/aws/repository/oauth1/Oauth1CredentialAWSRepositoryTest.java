package com.coderstower.socialmediapubisher.springpublisher.main.aws.repository.oauth1;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.repository.credential.Oauth1Credentials;
import com.coderstower.socialmediapubisher.springpublisher.main.aws.repository.oauth1.Oauth1CredentialAWSRepository;
import com.coderstower.socialmediapubisher.springpublisher.main.aws.repository.oauth1.Oauth1CredentialDynamo;
import com.coderstower.socialmediapubisher.springpublisher.main.aws.repository.oauth1.Oauth1CredentialDynamoRepository;
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
class Oauth1CredentialAWSRepositoryTest {
    @Mock
    private Oauth1CredentialDynamoRepository oauth1CredentialDynamoRepository;
    @InjectMocks
    private Oauth1CredentialAWSRepository oauth1CredentialAWSRepository;

    @Test
    public void getCredentials(){
        when(oauth1CredentialDynamoRepository.findById("twitter")).thenReturn(Optional.of(Oauth1CredentialDynamo.builder()
                .id("twitter")
                .accessToken("accessToken")
                .tokenSecret("tokenSecret")
                .consumerKey("consumerKey")
                .consumerSecret("consumerSecret")
                .build()));

        Optional<Oauth1Credentials> credentials = oauth1CredentialAWSRepository.getCredentials("twitter");

        assertThat(credentials).isEqualTo(Optional.of(Oauth1Credentials.builder()
                .id("twitter")
                .accessToken("accessToken")
                .tokenSecret("tokenSecret")
                .consumerKey("consumerKey")
                .consumerSecret("consumerSecret")
                .build()));
    }

}