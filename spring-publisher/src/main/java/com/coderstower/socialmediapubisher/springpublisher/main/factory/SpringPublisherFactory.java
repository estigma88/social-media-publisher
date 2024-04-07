package com.coderstower.socialmediapubisher.springpublisher.main.factory;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.PostPublisher;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository.PostRepository;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.SocialMediaPublisher;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.security.OAuth2CredentialsManager;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.security.repository.OAuth1CredentialsRepository;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.security.repository.OAuth2CredentialsRepository;
import com.coderstower.socialmediapubisher.springpublisher.main.socialmedia.linkedin.LinkedInPublisher;
import com.coderstower.socialmediapubisher.springpublisher.main.socialmedia.twitter.TwitterPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

import java.time.Clock;
import java.util.List;


@Configuration
@EnableConfigurationProperties({SocialMediaPublisherProperties.class})
public class SpringPublisherFactory {
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public PostPublisher postPublisher(List<SocialMediaPublisher> socialMediaPublishers, PostRepository postRepository, Clock clock, SocialMediaPublisherProperties socialMediaPublisherProperties) {
        return new PostPublisher(socialMediaPublishers, postRepository, clock, socialMediaPublisherProperties.getGroups());
    }

    @Bean
    @Profile("twitter")
    public TwitterPublisher twitterPublisher(OAuth1CredentialsRepository oauth1CredentialsRepository, Twitter twitter, Clock clock) {
        return new TwitterPublisher("twitter", oauth1CredentialsRepository, twitter, clock);
    }

    @Bean
    @Profile("linkedin")
    public LinkedInPublisher linkedInPublisher(OAuth2CredentialsRepository oauth2CredentialsRepository, RestTemplate restTemplate, Clock clock, SocialMediaPublisherProperties socialMediaPublisherProperties) {
        return new LinkedInPublisher("linkedin", oauth2CredentialsRepository, restTemplate, clock, socialMediaPublisherProperties.getCredentials().getLoginUrl());
    }

    @Bean
    public OAuth2CredentialsManager oAuth2CredentialsManager(OAuth2CredentialsRepository oAuth2CredentialsRepository, SocialMediaPublisherProperties socialMediaPublisherProperties) {
        return new OAuth2CredentialsManager(oAuth2CredentialsRepository, socialMediaPublisherProperties.getPrincipalNamesAllowed());
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Twitter twitter() {
        return TwitterFactory.getSingleton();
    }
}
