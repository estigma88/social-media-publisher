package com.coderstower.socialmediapubisher.springpublisher.main.aws.repository.oauth2;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.repository.credential.Oauth2Credentials;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.repository.credential.Oauth2CredentialsRepository;

import java.util.Optional;

public class Oauth2CredentialAWSRepository implements Oauth2CredentialsRepository {
    private final Oauth2CredentialDynamoRepository oauth2CredentialDynamoRepository;

    public Oauth2CredentialAWSRepository(Oauth2CredentialDynamoRepository oauth2CredentialDynamoRepository) {
        this.oauth2CredentialDynamoRepository = oauth2CredentialDynamoRepository;
    }

    @Override
    public Optional<Oauth2Credentials> getCredentials(String id) {
        return oauth2CredentialDynamoRepository.findById(id).map(this::convert);
    }

    private Oauth2Credentials convert(Oauth2CredentialDynamo oauth2CredentialDynamo) {
        return Oauth2Credentials.builder()
                .accessToken(oauth2CredentialDynamo.getAccessToken())
                .id(oauth2CredentialDynamo.getId())
                .expirationDate(oauth2CredentialDynamo.getExpirationDate())
                .build();
    }
}
