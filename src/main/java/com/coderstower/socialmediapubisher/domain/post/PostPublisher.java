package com.coderstower.socialmediapubisher.domain.post;

import com.coderstower.socialmediapubisher.domain.post.repository.Post;
import com.coderstower.socialmediapubisher.domain.post.repository.PostRepository;
import com.coderstower.socialmediapubisher.domain.post.socialmedia.Acknowledge;
import com.coderstower.socialmediapubisher.domain.post.socialmedia.Publication;
import com.coderstower.socialmediapubisher.domain.post.socialmedia.SocialMediaPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
public class PostPublisher {
    private final List<SocialMediaPublisher> socialMediaPublishers;
    private final PostRepository postRepository;
    private final Clock clock;
    private final MailSender mailSender;
    private final String senderEmail;
    private final String receiverEmail;

    public PostPublisher(List<SocialMediaPublisher> socialMediaPublishers, PostRepository postRepository, Clock clock, MailSender mailSender, String senderEmail, String receiverEmail) {
        this.socialMediaPublishers = socialMediaPublishers;
        this.postRepository = postRepository;
        this.clock = clock;
        this.mailSender = mailSender;
        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
    }

    public Post publishNext(String group) {
        try{
            ping(socialMediaPublishers, group);

            Post nextPost = postRepository.getNextToPublish(group)
                    .orElseThrow(() -> new IllegalStateException("There is not next post to publish"));

            List<Publication> publishedPosts = publish(socialMediaPublishers, nextPost);

            if (publishedWellOK(publishedPosts)) {
                Post toUpdate = nextPost.updateLastDatePublished(LocalDateTime.now(clock));

                Post postUpdated = postRepository.update(toUpdate)
                        .updatePublications(publishedPosts);

                SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
                simpleMailMessage.setFrom(senderEmail);
                simpleMailMessage.setTo(receiverEmail);
                simpleMailMessage.setSubject("SocialMediaPublisher: Success publishing group " + group);
                simpleMailMessage.setText(postUpdated.toString());
                mailSender.send(simpleMailMessage);

                return postUpdated;
            } else {
                throw new IllegalStateException("Error publishing the post: " + nextPost
                        .updatePublications(publishedPosts));
            }
        } catch (Exception e) {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(senderEmail);
            simpleMailMessage.setTo(receiverEmail);
            simpleMailMessage.setSubject("SocialMediaPublisher: Error publishing group " + group);
            simpleMailMessage.setText(e.getMessage());
            mailSender.send(simpleMailMessage);

            throw e;
        }
    }

    private boolean publishedWellOK(List<Publication> publishedPosts) {
        return publishedPosts.stream()
                .allMatch(publishedPost -> publishedPost.getStatus().equals(Publication.Status.SUCCESS));
    }

    private List<Publication> publish(List<SocialMediaPublisher> socialMediaPublishers, Post nextPost) {
        return socialMediaPublishers.stream()
                .flatMap(socialMediaPublisher -> socialMediaPublisher.publish(nextPost).stream())
                .collect(Collectors.toList());
    }

    private void ping(List<SocialMediaPublisher> socialMediaPublishers, String group) {
        for (SocialMediaPublisher socialMediaPublisher : socialMediaPublishers) {
            Acknowledge ping = socialMediaPublisher.ping(group);

            if (!Acknowledge.Status.SUCCESS.equals(ping.getStatus())) {
                throw new IllegalStateException("Ping to " + socialMediaPublisher.getName() + " failed: " + ping);
            }
        }
    }
}
