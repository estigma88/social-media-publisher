package com.coderstower.socialmediapubisher.springpublisher.main.socialmedia.linkedin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpecificContent {
    @JsonProperty("com.linkedin.ugc.ShareContent")
    private final ShareContent shareContent;
}
