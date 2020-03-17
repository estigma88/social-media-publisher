package com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Acknowledge {
    private final Status status;

    public static enum Status{
        SUCCESS
    }
}
