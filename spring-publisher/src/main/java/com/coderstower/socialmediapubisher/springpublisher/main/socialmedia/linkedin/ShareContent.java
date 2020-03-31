package com.coderstower.socialmediapubisher.springpublisher.main.socialmedia.linkedin;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Data
@Builder
public class ShareContent {
    private final Text shareCommentary;
    private final String shareMediaCategory;
    @Singular("media")
    private final List<Media> media;
}
