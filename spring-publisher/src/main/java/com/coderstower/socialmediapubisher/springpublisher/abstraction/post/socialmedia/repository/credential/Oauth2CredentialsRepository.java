package com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.repository.credential;

import java.util.Optional;

public interface Oauth2CredentialsRepository {
    Optional<Oauth2Credentials> getCredentials(String id);
}
