package com.coderstower.socialmediapubisher.springpublisher.main.aws.repository.post;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface PostDynamoRepository extends
        CrudRepository<PostDynamo, String> {
}
