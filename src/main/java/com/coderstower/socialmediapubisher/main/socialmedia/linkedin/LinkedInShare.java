package com.coderstower.socialmediapubisher.main.socialmedia.linkedin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LinkedInShare {
    private final String commentary;
    private final Distribution distribution;
    private final String author;
    private final String lifecycleState;
    private final Content content;
    private final String visibility;
}
