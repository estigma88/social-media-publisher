package com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Publication {
    private final String id;
    private final Status status;
    private final String publisher;
    private final String credentialId;
    private final LocalDateTime publishedDate;

    public enum Status{
        SUCCESS, FAILURE
    }
}
