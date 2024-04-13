package com.coderstower.socialmediapubisher.springpublisher.main.socialmedia.linkedin;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Data
@Builder
public class ArticleContent {
    private final String title;
    private final String description;
    private final String source;
}
