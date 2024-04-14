package com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository.Post;

import java.util.List;

public interface SocialMediaPublisher {
    String getName();
    Acknowledge ping();
    List<Publication> publish(Post post);
}
