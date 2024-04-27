package com.coderstower.socialmediapubisher.application.factory;

import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "social-media-publisher")
@Data
@Builder
public class SocialMediaPublisherProperties {
    private final Map<String, String> principalNamesAllowed;
    private final CredentialsProperties credentials;
    private final LinkedInProperties linkedIn;
}
