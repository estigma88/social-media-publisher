package com.coderstower.socialmediapubisher.abstraction.post.repository;

import java.util.Optional;

public interface PostRepository {
    Optional<Post> getNextToPublish(String group);
    Post update(Post post);
}
