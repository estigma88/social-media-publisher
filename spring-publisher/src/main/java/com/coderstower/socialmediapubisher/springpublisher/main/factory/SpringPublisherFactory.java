package com.coderstower.socialmediapubisher.springpublisher.main.factory;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.PostPublisher;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository.PostRepository;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.SocialMediaPublisher;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.repository.credential.Oauth1CredentialsRepository;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.repository.credential.Oauth2CredentialsRepository;
import com.coderstower.socialmediapubisher.springpublisher.main.socialmedia.linkedin.LinkedInPublisher;
import com.coderstower.socialmediapubisher.springpublisher.main.socialmedia.twitter.TwitterPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
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
    public LinkedInPublisher linkedInPublisher(Oauth2CredentialsRepository oauth2CredentialsRepository, RestTemplate restTemplate, Clock clock){
        return new LinkedInPublisher(oauth2CredentialsRepository, restTemplate, clock);
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean
    public Twitter twitter() {
        return TwitterFactory.getSingleton();
    }
}
