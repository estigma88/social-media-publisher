package com.coderstower.socialmediapubisher.domain.post.socialmedia;

import com.coderstower.socialmediapubisher.domain.post.repository.Post;

import java.util.List;

public interface SocialMediaPublisher {
    String getName();
    Acknowledge ping(String group);
    List<Publication> publish(Post post);
}
