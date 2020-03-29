package com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository.Post;

public interface SocialMediaPublisher {
    String getName();
    Acknowledge ping();
    Publication publish(Post post);
}
