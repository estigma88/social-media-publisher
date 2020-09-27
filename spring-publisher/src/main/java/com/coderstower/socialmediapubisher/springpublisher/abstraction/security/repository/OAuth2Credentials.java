package com.coderstower.socialmediapubisher.springpublisher.abstraction.security.repository;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OAuth2Credentials {
    private final String id;
    private final String accessToken;
    private final LocalDateTime expirationDate;

    public OAuth2Credentials update(String accessToken, LocalDateTime expirationDate){
        return OAuth2Credentials.builder()
                .id(id)
                .accessToken(accessToken)
                .expirationDate(expirationDate)
                .build();
    }
}
