package com.coderstower.socialmediapubisher.springpublisher.abstraction.security.repository;

import java.util.List;
import java.util.Optional;

public interface OAuth2CredentialsRepository {
    OAuth2Credentials save(OAuth2Credentials oAuth2Credentials);

    OAuth2Credentials update(OAuth2Credentials oAuth2Credentials);

    Optional<OAuth2Credentials> getCredentials(String id);
    List<OAuth2Credentials> findAll();
}
