package com.coderstower.socialmediapubisher.application.socialmedia.linkedin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Media {
    private final String status;
    private final Text description;
    private final Text title;
    private final String originalUrl;
}
