package com.coderstower.socialmediapubisher.springpublisher.main.factory;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.PostPublisher;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository.PostRepository;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.SocialMediaPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;


@Configuration
public class SpringPublisherFactory {
    @Bean
    public PostPublisher postPublisher(Map<String, SocialMediaPublisher> socialMediaPublishers, PostRepository postRepository) {
        return new PostPublisher(socialMediaPublishers, postRepository);
    }
}
