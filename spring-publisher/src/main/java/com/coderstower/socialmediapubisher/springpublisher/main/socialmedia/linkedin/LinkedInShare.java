package com.coderstower.socialmediapubisher.springpublisher.main.socialmedia.linkedin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LinkedInShare {
    private final String author;
    private final String lifecycleState;
    private final SpecificContent specificContent;
    private final Visibility visibility;
}
