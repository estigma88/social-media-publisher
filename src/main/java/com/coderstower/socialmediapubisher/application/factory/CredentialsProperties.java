package com.coderstower.socialmediapubisher.application.factory;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.util.UriTemplate;

@Data
@Builder
public class CredentialsProperties {
    private final UriTemplate loginUrl;
}
