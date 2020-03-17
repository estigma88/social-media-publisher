package com.coderstower.socialmediapubisher.springpublisher.abstraction.post;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository.Post;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository.PostRepository;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.Acknowledge;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.PublishedPost;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.SocialMediaPublisher;

import java.util.List;
import java.util.stream.Collectors;

public class PostPublisher {
    private final List<SocialMediaPublisher> socialMediaPublishers;
    private final PostRepository postRepository;

    public PostPublisher(List<SocialMediaPublisher> socialMediaPublishers, PostRepository postRepository) {
        this.socialMediaPublishers = socialMediaPublishers;
        this.postRepository = postRepository;
    }

    public List<PublishedPost> publishNext() {
        ping(socialMediaPublishers);

        Post nextPost = postRepository.getNextToPublish().orElseThrow(() -> new IllegalStateException("There is not next post to publish"));

        return publish(socialMediaPublishers, nextPost);
    }

    private List<PublishedPost> publish(List<SocialMediaPublisher> socialMediaPublishers, Post nextPost) {
        return socialMediaPublishers.stream()
                .map(socialMediaPublisher -> socialMediaPublisher.publish(nextPost))
                .collect(Collectors.toList());
    }

    private void ping(List<SocialMediaPublisher> socialMediaPublishers) {
        for (SocialMediaPublisher socialMediaPublisher : socialMediaPublishers) {
            Acknowledge ping = socialMediaPublisher.ping();

            if (!Acknowledge.Status.SUCCESS.equals(ping.getStatus())) {
                throw new IllegalStateException("Ping to " + socialMediaPublisher.getName() + " failed: " + ping);
            }
        }
    }
}
