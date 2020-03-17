package com.coderstower.socialmediapubisher.springpublisher.main.socialmedia;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository.Post;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository.credential.Oauth1CredentialsRepository;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.Acknowledge;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.PublishedPost;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.SocialMediaPublisher;

public class TwitterPublisher implements SocialMediaPublisher {
    private static final String TWITTER = "twitter";
    private final Oauth1CredentialsRepository oauth1CredentialsRepository;

    public TwitterPublisher(Oauth1CredentialsRepository oauth1CredentialsRepository) {
        this.oauth1CredentialsRepository = oauth1CredentialsRepository;
    }

    @Override
    public String getName() {
        return TWITTER;
    }

    @Override
    public Acknowledge ping() {
        return null;
    }

    @Override
    public PublishedPost publish(Post post) {
        return null;
    }
}
