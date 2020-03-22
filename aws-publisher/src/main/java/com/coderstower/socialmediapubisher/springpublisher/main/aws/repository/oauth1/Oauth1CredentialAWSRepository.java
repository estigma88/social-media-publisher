package com.coderstower.socialmediapubisher.springpublisher.main.aws.repository.oauth1;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.repository.credential.Oauth1Credentials;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.repository.credential.Oauth1CredentialsRepository;

import java.util.Optional;

public class Oauth1CredentialAWSRepository implements Oauth1CredentialsRepository {
    private final Oauth1CredentialDynamoRepository oauth1CredentialDynamoRepository;

    public Oauth1CredentialAWSRepository(Oauth1CredentialDynamoRepository oauth1CredentialDynamoRepository) {
        this.oauth1CredentialDynamoRepository = oauth1CredentialDynamoRepository;
    }

    @Override
    public Optional<Oauth1Credentials> getCredentials(String id) {
        return oauth1CredentialDynamoRepository.findById(id).map(this::convert);
    }

    private Oauth1Credentials convert(Oauth1CredentialDynamo oauth1CredentialDynamo) {
        return Oauth1Credentials.builder()
                .accessToken(oauth1CredentialDynamo.getAccessToken())
                .consumerKey(oauth1CredentialDynamo.getConsumerKey())
                .consumerSecret(oauth1CredentialDynamo.getConsumerSecret())
                .id(oauth1CredentialDynamo.getId())
                .tokenSecret(oauth1CredentialDynamo.getTokenSecret())
                .build();
    }
}
