package com.coderstower.socialmediapubisher.application.aws.repository.oauth1;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuth1CredentialDynamoRepository extends CrudRepository<OAuth1CredentialDynamo, String> {
}
