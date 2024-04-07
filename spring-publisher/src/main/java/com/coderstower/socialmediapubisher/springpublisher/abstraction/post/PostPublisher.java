package com.coderstower.socialmediapubisher.springpublisher.abstraction.post;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository.Post;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository.PostRepository;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.Acknowledge;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.Publication;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.SocialMediaPublisher;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PostPublisher {
    private final List<SocialMediaPublisher> socialMediaPublishers;
    private final PostRepository postRepository;
    private final Clock clock;

    private final List<String> groupsToPublish;

    public PostPublisher(List<SocialMediaPublisher> socialMediaPublishers, PostRepository postRepository, Clock clock, List<String> groupsToPublish) {
        this.socialMediaPublishers = socialMediaPublishers;
        this.postRepository = postRepository;
        this.clock = clock;
        this.groupsToPublish = groupsToPublish;
    }

    public List<Post> publishNext() {
        ping(socialMediaPublishers);

        List<Post> posts = postRepository.findAll();
        List<Post> published = new ArrayList<>();

        for (String group : groupsToPublish) {
            Post nextPost = posts.stream()
                    .filter(post -> group.equals(post.getGroup()))
                    .min(Comparator.comparing(Post::getPublishedDate))
                    .orElseThrow(() -> new IllegalStateException("There is not next post to publish"));

            List<Publication> publishedPosts = publish(socialMediaPublishers, nextPost);

            if (publishedWellOK(publishedPosts)) {
                Post toUpdate = nextPost.updateLastDatePublished(LocalDateTime.now(clock));

                Post publishedPost = postRepository.update(toUpdate)
                        .updatePublications(publishedPosts);

                published.add(publishedPost);
            } else {
                throw new IllegalStateException("Error publishing the post: " + nextPost
                        .updatePublications(publishedPosts));
            }
        }

        return published;
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
