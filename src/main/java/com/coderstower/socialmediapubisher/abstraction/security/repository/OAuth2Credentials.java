package com.coderstower.socialmediapubisher.abstraction.security.repository;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OAuth2Credentials {
    private final String id;
    private final String accessToken;
    private final LocalDateTime expirationDate;
    private final List<String> allowedGroups;

    public OAuth2Credentials update(String accessToken, LocalDateTime expirationDate){
        return OAuth2Credentials.builder()
                .id(id)
                .accessToken(accessToken)
                .expirationDate(expirationDate)
                .build();
    }
}
