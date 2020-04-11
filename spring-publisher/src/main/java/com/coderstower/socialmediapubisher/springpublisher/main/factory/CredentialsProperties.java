package com.coderstower.socialmediapubisher.springpublisher.main.factory;

import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.ConstructorBinding;

@Data
@ConstructorBinding
@Builder
public class CredentialsProperties {
    private final String loginUrl;
}
