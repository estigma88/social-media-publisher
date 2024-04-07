package com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    List<Post> findAll();
    Optional<Post> getNextToPublish();
    Post update(Post post);
}
