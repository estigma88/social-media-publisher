package com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.repository.credential;

import java.util.Optional;

public interface Oauth1CredentialsRepository {
    Optional<Oauth1Credentials> getCredentials(String id);
}
