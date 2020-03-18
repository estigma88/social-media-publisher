package com.coderstower.socialmediapubisher.springpublisher.main.socialmedia;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository.Post;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.Acknowledge;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.PublishedPost;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.SocialMediaPublisher;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.repository.credential.Oauth1Credentials;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.repository.credential.Oauth1CredentialsRepository;
import lombok.extern.slf4j.Slf4j;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

import java.util.List;
import java.util.Objects;

@Slf4j
public class TwitterPublisher implements SocialMediaPublisher {
    private static final String TWITTER = "twitter";
    private final Oauth1CredentialsRepository oauth1CredentialsRepository;
    private final Twitter twitter;

    public TwitterPublisher(Oauth1CredentialsRepository oauth1CredentialsRepository, Twitter twitter) {
        this.oauth1CredentialsRepository = oauth1CredentialsRepository;
        this.twitter = twitter;
    }

    @Override
    public String getName() {
        return TWITTER;
    }

    @Override
    public Acknowledge ping() {
        Oauth1Credentials credentials = oauth1CredentialsRepository.getCredentials(TWITTER)
                .orElseThrow(() -> new IllegalArgumentException("The credentials for " + TWITTER + " doesn't exist"));

        AccessToken accessToken = new AccessToken(credentials.getAccessToken(), credentials.getTokenSecret());
        twitter.setOAuthConsumer(credentials.getConsumerKey(), credentials.getConsumerSecret());
        twitter.setOAuthAccessToken(accessToken);

        try {
            Paging paging = new Paging(1, 1);
            List<Status> statuses = twitter.getHomeTimeline(paging);

            if(Objects.nonNull(statuses)){
                return Acknowledge.builder()
                        .status(Acknowledge.Status.SUCCESS)
                        .build();
            }else{
                return Acknowledge.builder()
                        .status(Acknowledge.Status.FAILURE)
                        .build();
            }
        } catch (TwitterException e) {
            log.error("Error publishing to " + TWITTER, e);

            return Acknowledge.builder()
                    .status(Acknowledge.Status.FAILURE)
                    .exception(e)
                    .build();
        }
    }

    @Override
    public PublishedPost publish(Post post) {
        return PublishedPost.builder()
                .post(post)
                .status(PublishedPost.Status.SUCCESS)
                .publisher(TWITTER)
                .build();
    }
}
