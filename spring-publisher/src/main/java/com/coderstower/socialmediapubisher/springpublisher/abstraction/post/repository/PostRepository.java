package com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository;

import java.util.Optional;

public interface PostRepository {
    Optional<Post> getNextToPublish();
    Post update(Post post);
}
