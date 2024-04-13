package com.coderstower.socialmediapubisher.springpublisher.abstraction.post;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository.Post;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository.PostRepository;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.Acknowledge;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.Publication;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.SocialMediaPublisher;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PostPublisher {
    private final List<SocialMediaPublisher> socialMediaPublishers;
    private final PostRepository postRepository;
    private final Clock clock;

    public PostPublisher(List<SocialMediaPublisher> socialMediaPublishers, PostRepository postRepository, Clock clock) {
        this.socialMediaPublishers = socialMediaPublishers;
        this.postRepository = postRepository;
        this.clock = clock;
    }

    public Post publishNext(String group) {
        ping(socialMediaPublishers);

        Post nextPost = postRepository.getNextToPublish(group)
                .orElseThrow(() -> new IllegalStateException("There is not next post to publish"));

        List<Publication> publishedPosts = publish(socialMediaPublishers, nextPost);

        if (publishedWellOK(publishedPosts)) {
            Post toUpdate = nextPost.updateLastDatePublished(LocalDateTime.now(clock));

            return postRepository.update(toUpdate)
                    .updatePublications(publishedPosts);
        } else {
            throw new IllegalStateException("Error publishing the post: " + nextPost
                    .updatePublications(publishedPosts));
        }
    }

    private boolean publishedWellOK(List<Publication> publishedPosts) {
        return publishedPosts.stream()
                .allMatch(publishedPost -> publishedPost.getStatus().equals(Publication.Status.SUCCESS));
    }

    private List<Publication> publish(List<SocialMediaPublisher> socialMediaPublishers, Post nextPost) {
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
