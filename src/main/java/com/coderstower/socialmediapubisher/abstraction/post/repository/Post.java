package com.coderstower.socialmediapubisher.abstraction.post.repository;

import com.coderstower.socialmediapubisher.abstraction.post.socialmedia.Publication;
import lombok.Builder;
import lombok.Data;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Builder
public class Post {
    private static final String BASIC_FORMAT = "%s\n\n%s\n\n%s";
    private static final String BASIC_FORMAT_WITHOUT_URL = "%s\n\n%s";
    private final String id;
    private final String name;
    private final String description;
    private final List<String> tags;
    private final URL url;
    private final LocalDateTime publishedDate;
    private final List<Publication> publications;
    private final String group;

    public String basicFormat() {
        return String.format(BASIC_FORMAT,
                description,
                Optional.ofNullable(tags)
                        .orElse(List.of())
                        .stream()
                        .map(tag -> "#" + tag)
                        .collect(Collectors.joining(" "))
                , url);
    }

    public String basicFormatWithoutURL() {
        return String.format(BASIC_FORMAT_WITHOUT_URL,
                description,
                Optional.ofNullable(tags)
                        .orElse(List.of())
                        .stream()
                        .map(tag -> "#" + tag)
                        .collect(Collectors.joining(" ")));
    }

    public Post updateLastDatePublished(LocalDateTime publishedDate) {
        return Post.builder()
                .id(id)
                .name(name)
                .description(description)
                .tags(tags)
                .url(url)
                .publications(publications)
                .group(group)
                .publishedDate(publishedDate).build();
    }

    public Post updatePublications(List<Publication> publications) {
        return Post.builder()
                .id(id)
                .name(name)
                .description(description)
                .tags(tags)
                .url(url)
                .publications(publications)
                .group(group)
                .publishedDate(publishedDate).build();
    }
}
