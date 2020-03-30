package com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.repository.credential;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Oauth2Credentials {
    private final String id;
    private final String accessToken;
    private final LocalDateTime expirationDate;
}
