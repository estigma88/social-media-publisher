package com.coderstower.socialmediapubisher.domain.security;

import com.coderstower.socialmediapubisher.domain.security.repository.OAuth2Credentials;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OAuth2CredentialsUpdated {
    private final OAuth2Credentials oAuth2Credentials;
}
