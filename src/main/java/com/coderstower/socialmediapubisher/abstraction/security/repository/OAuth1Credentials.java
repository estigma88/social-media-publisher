package com.coderstower.socialmediapubisher.abstraction.security.repository;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OAuth1Credentials {
    private final String id;
    private final String consumerKey;
    private final String consumerSecret;
    private final String accessToken;
    private final String tokenSecret;
}
