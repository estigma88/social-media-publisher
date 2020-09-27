package com.coderstower.socialmediapubisher.springpublisher.abstraction.security.repository;

import java.util.Optional;

public interface OAuth1CredentialsRepository {
    Optional<OAuth1Credentials> getCredentials(String id);
}
