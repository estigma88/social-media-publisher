package com.coderstower.socialmediapubisher.springpublisher.main.factory;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.PostPublisher;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository.PostRepository;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.SocialMediaPublisher;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.repository.credential.Oauth1CredentialsRepository;
import com.coderstower.socialmediapubisher.springpublisher.main.socialmedia.TwitterPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

import java.time.Clock;
import java.util.List;


@Configuration
public class SpringPublisherFactory {
    @Bean
    public Clock clock(){
        return Clock.systemDefaultZone();
    }

    @Bean
    public PostPublisher postPublisher(List<SocialMediaPublisher> socialMediaPublishers, PostRepository postRepository, Clock clock) {
        return new PostPublisher(socialMediaPublishers, postRepository, clock);
    }

    @Bean
    public TwitterPublisher twitterPublisher(Oauth1CredentialsRepository oauth1CredentialsRepository, Twitter twitter, Clock clock) {
        return new TwitterPublisher(oauth1CredentialsRepository, twitter, clock);
    }

    @Bean
    public Twitter twitter() {
        return TwitterFactory.getSingleton();
    }
}
