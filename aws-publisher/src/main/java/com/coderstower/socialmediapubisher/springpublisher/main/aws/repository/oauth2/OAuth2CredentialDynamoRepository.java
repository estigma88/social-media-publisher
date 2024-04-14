package com.coderstower.socialmediapubisher.springpublisher.main.aws.repository.oauth2;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@EnableScan
public interface OAuth2CredentialDynamoRepository extends CrudRepository<OAuth2CredentialDynamo, String> {
}
