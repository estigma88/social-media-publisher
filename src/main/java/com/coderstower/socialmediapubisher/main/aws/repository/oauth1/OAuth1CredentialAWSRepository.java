package com.coderstower.socialmediapubisher.main.aws.repository.oauth1;

import com.coderstower.socialmediapubisher.domain.security.repository.OAuth1Credentials;
import com.coderstower.socialmediapubisher.domain.security.repository.OAuth1CredentialsRepository;

import java.util.Optional;

public class OAuth1CredentialAWSRepository implements OAuth1CredentialsRepository {
    private final OAuth1CredentialDynamoRepository oauth1CredentialDynamoRepository;

    public OAuth1CredentialAWSRepository(OAuth1CredentialDynamoRepository oauth1CredentialDynamoRepository) {
        this.oauth1CredentialDynamoRepository = oauth1CredentialDynamoRepository;
    }

    @Override
    public Optional<OAuth1Credentials> getCredentials(String id) {
        return oauth1CredentialDynamoRepository.findById(id).map(this::convert);
    }

    private OAuth1Credentials convert(OAuth1CredentialDynamo oauth1CredentialDynamo) {
        return OAuth1Credentials.builder()
                .accessToken(oauth1CredentialDynamo.getAccessToken())
                .consumerKey(oauth1CredentialDynamo.getConsumerKey())
                .consumerSecret(oauth1CredentialDynamo.getConsumerSecret())
                .id(oauth1CredentialDynamo.getId())
                .tokenSecret(oauth1CredentialDynamo.getTokenSecret())
                .build();
    }
}
