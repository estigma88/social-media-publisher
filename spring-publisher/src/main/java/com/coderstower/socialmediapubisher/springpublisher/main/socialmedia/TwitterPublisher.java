package com.coderstower.socialmediapubisher.springpublisher.main.socialmedia;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.SocialMediaPublisher;

public class TwitterPublisher implements SocialMediaPublisher {
    @Override
    public boolean ping() {
        return false;
    }
}
