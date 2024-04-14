package com.coderstower.socialmediapubisher.application.aws.repository.oauth2;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface OAuth2CredentialDynamoRepository extends CrudRepository<OAuth2CredentialDynamo, String> {
}
