package com.coderstower.socialmediapubisher.main.factory;

import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.Map;

@ConfigurationProperties(prefix = "social-media-publisher")
@Data
@ConstructorBinding
@Builder
public class SocialMediaPublisherProperties {
    private final Map<String, String> principalNamesAllowed;
    private final CredentialsProperties credentials;
}
