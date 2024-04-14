package com.coderstower.socialmediapubisher.abstraction.security.repository;

import java.util.Optional;

public interface OAuth1CredentialsRepository {
    Optional<OAuth1Credentials> getCredentials(String id);
}
