package com.coderstower.socialmediapubisher.main.socialmedia.linkedin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArticleContent {
    private final String title;
    private final String description;
    private final String source;
}
