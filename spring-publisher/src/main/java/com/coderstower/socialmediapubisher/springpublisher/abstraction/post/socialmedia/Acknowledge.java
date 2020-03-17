package com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Acknowledge {
    private final Status status;
    private final Exception exception;

    public enum Status{
        SUCCESS,
        FAILURE
    }
}
