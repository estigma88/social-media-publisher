package com.coderstower.socialmediapubisher.springpublisher.main.post.repository;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PostH2Repository extends CrudRepository<PostEntity, String> {
    Optional<PostEntity> findFirstByOrderByLastDatePublishedAsc();
}
