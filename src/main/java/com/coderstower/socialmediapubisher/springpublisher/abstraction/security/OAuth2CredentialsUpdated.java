package com.coderstower.socialmediapubisher.springpublisher.abstraction.security;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.security.repository.OAuth2Credentials;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OAuth2CredentialsUpdated {
    private final OAuth2Credentials oAuth2Credentials;
}
