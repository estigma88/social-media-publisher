package com.coderstower.socialmediapubisher.springpublisher.main.post.repository.credential;

import com.coderstower.socialmediapubisher.springpublisher.main.post.repository.PostEntity;
import org.springframework.data.repository.CrudRepository;

public interface Oauth2CredentialH2Repository extends CrudRepository<Oauth2CredentialEntity, String> {
}
