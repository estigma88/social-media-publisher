package com.coderstower.socialmediapubisher.application.factory;

import com.coderstower.socialmediapubisher.application.socialmedia.twitter.TwitterPublisher;
import com.coderstower.socialmediapubisher.domain.post.PostPublisher;
import com.coderstower.socialmediapubisher.domain.post.repository.PostRepository;
import com.coderstower.socialmediapubisher.domain.post.socialmedia.SocialMediaPublisher;
import com.coderstower.socialmediapubisher.domain.security.repository.OAuth1CredentialsRepository;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailSender;
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
    public PostPublisher postPublisher(List<SocialMediaPublisher> socialMediaPublishers, PostRepository postRepository, Clock clock, MailSender mailSender, SocialMediaPublisherProperties socialMediaPublisherProperties) {
        return new PostPublisher(socialMediaPublishers, postRepository, clock, mailSender, socialMediaPublisherProperties.getPost().getMail().getSenderEmail(), socialMediaPublisherProperties.getPost().getMail().getReceiverEmail());
    }

    @Bean
    @Profile("twitter")
    public TwitterPublisher twitterPublisher(OAuth1CredentialsRepository oauth1CredentialsRepository, Twitter twitter, Clock clock) {
        return new TwitterPublisher("twitter", oauth1CredentialsRepository, twitter, clock);
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
