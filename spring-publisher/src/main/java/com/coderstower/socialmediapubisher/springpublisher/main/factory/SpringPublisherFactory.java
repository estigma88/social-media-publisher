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

import java.util.List;


@Configuration
public class SpringPublisherFactory {
    @Bean
    public PostPublisher postPublisher(List<SocialMediaPublisher> socialMediaPublishers, PostRepository postRepository) {
        return new PostPublisher(socialMediaPublishers, postRepository);
    }

    @Bean
    public TwitterPublisher twitterPublisher(Oauth1CredentialsRepository oauth1CredentialsRepository, Twitter twitter) {
        return new TwitterPublisher(oauth1CredentialsRepository, twitter);
    }

    @Bean
    public Twitter twitter() {
        return TwitterFactory.getSingleton();
    }

    /*@Bean
    public PostsController postsController(PostPublisher postPublisher) {
        return new PostsController(postPublisher);
    }*/
}
