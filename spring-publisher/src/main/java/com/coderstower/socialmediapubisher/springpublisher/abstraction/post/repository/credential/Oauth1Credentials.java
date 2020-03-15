package com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository.credential;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Oauth1Credentials {
    private final String id;
    private final String consumerKey;
    private final String consumerSecret;
    private final String accessToken;
    private final String tokenSecret;
}
