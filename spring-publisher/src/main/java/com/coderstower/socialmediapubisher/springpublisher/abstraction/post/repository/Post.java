package com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository;

import lombok.Builder;
import lombok.Data;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class Post {
    private final String id;
    private final String name;
    private final String description;
    private final List<String> tags;
    private final URL url;
    private final LocalDateTime lastDatePublished;
}
