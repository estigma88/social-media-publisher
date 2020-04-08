package com.coderstower.socialmediapubisher.springpublisher.main.aws.repository.oauth2;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.security.repository.OAuth2Credentials;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.security.repository.OAuth2CredentialsRepository;

import java.util.Optional;

public class OAuth2CredentialAWSRepository implements OAuth2CredentialsRepository {
    private final OAuth2CredentialDynamoRepository oauth2CredentialDynamoRepository;

    public OAuth2CredentialAWSRepository(OAuth2CredentialDynamoRepository oauth2CredentialDynamoRepository) {
        this.oauth2CredentialDynamoRepository = oauth2CredentialDynamoRepository;
    }

    @Override
    public OAuth2Credentials update(OAuth2Credentials oAuth2Credentials) {
        return convert(oauth2CredentialDynamoRepository.save(convert(oAuth2Credentials)));
    }

    @Override
    public Optional<OAuth2Credentials> getCredentials(String id) {
        return oauth2CredentialDynamoRepository.findById(id).map(this::convert);
    }

    private OAuth2CredentialDynamo convert(OAuth2Credentials oAuth2Credentials) {
        return OAuth2CredentialDynamo.builder()
                .accessToken(oAuth2Credentials.getAccessToken())
                .id(oAuth2Credentials.getId())
                .expirationDate(oAuth2Credentials.getExpirationDate())
                .build();
    }

    private OAuth2Credentials convert(OAuth2CredentialDynamo oauth2CredentialDynamo) {
        return OAuth2Credentials.builder()
                .accessToken(oauth2CredentialDynamo.getAccessToken())
                .id(oauth2CredentialDynamo.getId())
                .expirationDate(oauth2CredentialDynamo.getExpirationDate())
                .build();
    }
}
