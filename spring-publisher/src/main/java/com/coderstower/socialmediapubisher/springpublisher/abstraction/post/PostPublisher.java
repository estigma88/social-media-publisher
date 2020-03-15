package com.coderstower.socialmediapubisher.springpublisher.abstraction.post;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository.PostRepository;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.SocialMediaPublisher;

import java.util.Map;

public class PostPublisher {
    private final Map<String, SocialMediaPublisher> socialMediaPublishers;
    private final PostRepository postRepository;

    public PostPublisher(Map<String, SocialMediaPublisher> socialMediaPublishers, PostRepository postRepository) {
        this.socialMediaPublishers = socialMediaPublishers;
        this.postRepository = postRepository;
    }

    public void next() {

    }
}
