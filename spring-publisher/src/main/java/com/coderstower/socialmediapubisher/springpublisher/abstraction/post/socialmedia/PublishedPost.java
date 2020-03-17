package com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository.Post;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PublishedPost {
    private final Post post;
    private final Status status;
    private final String publisher;

    public enum Status{
        SUCCESS
    }
}
