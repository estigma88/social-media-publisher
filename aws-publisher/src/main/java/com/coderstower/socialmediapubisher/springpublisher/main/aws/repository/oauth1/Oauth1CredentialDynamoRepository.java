package com.coderstower.socialmediapubisher.springpublisher.main.aws.repository.oauth1;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Oauth1CredentialDynamoRepository extends CrudRepository<Oauth1CredentialDynamo, String> {
}
