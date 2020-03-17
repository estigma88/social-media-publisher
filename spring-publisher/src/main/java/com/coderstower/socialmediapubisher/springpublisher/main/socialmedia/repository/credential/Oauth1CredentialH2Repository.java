package com.coderstower.socialmediapubisher.springpublisher.main.socialmedia.repository.credential;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Oauth1CredentialH2Repository extends CrudRepository<Oauth1CredentialEntity, String> {
}
