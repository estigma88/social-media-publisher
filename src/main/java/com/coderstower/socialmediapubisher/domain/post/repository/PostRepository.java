package com.coderstower.socialmediapubisher.domain.post.repository;

import java.util.Optional;

public interface PostRepository {
    Optional<Post> getNextToPublish(String group);
    Post update(Post post);
}
