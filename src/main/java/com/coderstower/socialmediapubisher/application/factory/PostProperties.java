package com.coderstower.socialmediapubisher.application.factory;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.util.UriTemplate;

@Data
@Builder
public class PostProperties {
    private final MailProperties mail;
}
