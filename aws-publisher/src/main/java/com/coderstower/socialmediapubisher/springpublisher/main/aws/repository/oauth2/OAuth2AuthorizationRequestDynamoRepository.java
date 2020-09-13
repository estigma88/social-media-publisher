package com.coderstower.socialmediapubisher.springpublisher.main.aws.repository.oauth2;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuth2AuthorizationRequestDynamoRepository extends CrudRepository<OAuth2AuthorizationRequestDynamo, String> {
}
