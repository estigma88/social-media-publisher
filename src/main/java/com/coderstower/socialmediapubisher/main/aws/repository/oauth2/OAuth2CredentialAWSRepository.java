package com.coderstower.socialmediapubisher.main.aws.repository.oauth2;

import com.coderstower.socialmediapubisher.abstraction.security.repository.OAuth2CredentialsRepository;
import com.coderstower.socialmediapubisher.abstraction.security.repository.OAuth2Credentials;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class OAuth2CredentialAWSRepository implements OAuth2CredentialsRepository {
    private final OAuth2CredentialDynamoRepository oauth2CredentialDynamoRepository;

    public OAuth2CredentialAWSRepository(OAuth2CredentialDynamoRepository oauth2CredentialDynamoRepository) {
        this.oauth2CredentialDynamoRepository = oauth2CredentialDynamoRepository;
    }

    @Override
    public OAuth2Credentials save(OAuth2Credentials oAuth2Credentials) {
        return convert(oauth2CredentialDynamoRepository.save(convert(oAuth2Credentials)));
    }

    @Override
    public OAuth2Credentials update(OAuth2Credentials oAuth2Credentials) {
        return convert(oauth2CredentialDynamoRepository.save(convert(oAuth2Credentials)));
    }

    @Override
    public Optional<OAuth2Credentials> getCredentials(String id) {
        return oauth2CredentialDynamoRepository.findById(id).map(this::convert);
    }

    @Override
    public List<OAuth2Credentials> findAll() {
        return StreamSupport.stream(oauth2CredentialDynamoRepository.findAll().spliterator(), false)
                .map(this::convert)
                .collect(Collectors.toList());
    }

    private OAuth2CredentialDynamo convert(OAuth2Credentials oAuth2Credentials) {
        return OAuth2CredentialDynamo.builder()
                .accessToken(oAuth2Credentials.getAccessToken())
                .id(oAuth2Credentials.getId())
                .expirationDate(oAuth2Credentials.getExpirationDate())
                .allowedGroups(oAuth2Credentials.getAllowedGroups())
                .build();
    }

    private OAuth2Credentials convert(OAuth2CredentialDynamo oauth2CredentialDynamo) {
        return OAuth2Credentials.builder()
                .accessToken(oauth2CredentialDynamo.getAccessToken())
                .id(oauth2CredentialDynamo.getId())
                .expirationDate(oauth2CredentialDynamo.getExpirationDate())
                .allowedGroups(oauth2CredentialDynamo.getAllowedGroups())
                .build();
    }
}
