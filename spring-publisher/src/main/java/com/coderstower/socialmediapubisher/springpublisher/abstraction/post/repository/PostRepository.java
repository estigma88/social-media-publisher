package com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository;

import java.util.List;

public interface PostRepository {
    List<Post> findAll();
    Post update(Post post);
}
