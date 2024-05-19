package com.coderstower.socialmediapubisher.application.factory;

import lombok.Builder;
import lombok.Data;

import java.net.URI;
@Data
@Builder
public class LinkedInProperties {
    private final URI baseURL;
}
