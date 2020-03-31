package com.coderstower.socialmediapubisher.springpublisher.main.aws.repository.oauth2;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Oauth2CredentialDynamoRepository extends CrudRepository<Oauth2CredentialDynamo, String> {
}
