package com.coderstower.socialmediapubisher.application.socialmedia.linkedin;

import com.coderstower.socialmediapubisher.application.factory.SocialMediaPublisherProperties;
import com.coderstower.socialmediapubisher.domain.security.OAuth2CredentialsManager;
import com.coderstower.socialmediapubisher.domain.security.repository.OAuth2CredentialsRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

import java.time.Clock;


@Configuration
@Profile("linkedin")
public class LinkedInPublisherFactory {

    @Bean
    public LinkedInPublisher linkedInPublisher(OAuth2CredentialsRepository oauth2CredentialsRepository, RestTemplate restTemplate, Clock clock, SocialMediaPublisherProperties socialMediaPublisherProperties) {
        return new LinkedInPublisher("linkedin", oauth2CredentialsRepository, restTemplate, clock, socialMediaPublisherProperties.getCredentials().getLoginUrl(), socialMediaPublisherProperties.getLinkedIn().getBaseURL());
    }

    @Bean
    public OAuth2CredentialsManager oAuth2CredentialsManager(OAuth2CredentialsRepository oAuth2CredentialsRepository, SocialMediaPublisherProperties socialMediaPublisherProperties) {
        return new OAuth2CredentialsManager(oAuth2CredentialsRepository, socialMediaPublisherProperties.getPrincipalNamesAllowed());
    }
}
