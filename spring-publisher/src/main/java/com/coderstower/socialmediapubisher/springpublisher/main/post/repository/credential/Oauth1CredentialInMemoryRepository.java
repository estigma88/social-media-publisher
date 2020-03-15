package com.coderstower.socialmediapubisher.springpublisher.main.post.repository.credential;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository.credential.Oauth1Credentials;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository.credential.Oauth1CredentialsRepository;

import java.util.Optional;

public class Oauth1CredentialInMemoryRepository implements Oauth1CredentialsRepository {
    private final Oauth1CredentialH2Repository oauth1CredentialH2Repository;

    public Oauth1CredentialInMemoryRepository(Oauth1CredentialH2Repository oauth1CredentialH2Repository) {
        this.oauth1CredentialH2Repository = oauth1CredentialH2Repository;
    }

    @Override
    public Optional<Oauth1Credentials> getCredentials(String id) {
        return oauth1CredentialH2Repository.findById(id).map(this::convert);
    }

    private Oauth1Credentials convert(Oauth1CredentialEntity oauth1CredentialEntity) {
        return Oauth1Credentials.builder()
                .accessToken(oauth1CredentialEntity.getAccessToken())
                .consumerKey(oauth1CredentialEntity.getConsumerKey())
                .consumerSecret(oauth1CredentialEntity.getConsumerSecret())
                .id(oauth1CredentialEntity.getId())
                .tokenSecret(oauth1CredentialEntity.getTokenSecret())
                .build();
    }
}
