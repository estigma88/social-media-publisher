package com.coderstower.socialmediapubisher.springpublisher.main.factory;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository.PostRepository;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository.credential.Oauth1CredentialsRepository;
import com.coderstower.socialmediapubisher.springpublisher.main.post.repository.PostH2Repository;
import com.coderstower.socialmediapubisher.springpublisher.main.post.repository.PostInMemoryRepository;
import com.coderstower.socialmediapubisher.springpublisher.main.post.repository.credential.Oauth1CredentialH2Repository;
import com.coderstower.socialmediapubisher.springpublisher.main.post.repository.credential.Oauth1CredentialInMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.coderstower.socialmediapubisher.springpublisher.main.post.repository")
public class SpringPublisherRepositoryFactory {
    @Bean
    public Oauth1CredentialsRepository oauth1CredentialInMemoryRepository(Oauth1CredentialH2Repository oauth1CredentialH2Repository) {
        return new Oauth1CredentialInMemoryRepository(oauth1CredentialH2Repository);
    }

    @Bean
    public PostRepository postRepository(PostH2Repository postH2Repository) {
        return new PostInMemoryRepository(postH2Repository);
    }
}
