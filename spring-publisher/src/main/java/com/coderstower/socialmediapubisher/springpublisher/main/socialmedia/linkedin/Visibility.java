package com.coderstower.socialmediapubisher.springpublisher.main.socialmedia.linkedin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Visibility {
    @JsonProperty("com.linkedin.ugc.MemberNetworkVisibility")
    private final String memberNetworkVisibility;
}
