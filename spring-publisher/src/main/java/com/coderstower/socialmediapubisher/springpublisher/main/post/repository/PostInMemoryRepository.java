package com.coderstower.socialmediapubisher.springpublisher.main.post.repository;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository.Post;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository.PostRepository;

import java.util.Optional;

public class PostInMemoryRepository implements PostRepository {
    private final PostH2Repository postH2Repository;

    public PostInMemoryRepository(PostH2Repository postH2Repository) {
        this.postH2Repository = postH2Repository;
    }

    @Override
    public Optional<Post> getNextToPublish() {
        return postH2Repository.findTopByLastDatePublishedOrderByLastDatePublished()
                .map(this::convert);
    }

    @Override
    public Post update(Post post) {
        return convert(postH2Repository.save(convert(post)));
    }

    private PostEntity convert(Post post) {
        return PostEntity.builder()
                .id(post.getId())
                .description(post.getDescription())
                .lastDatePublished(post.getLastDatePublished())
                .name(post.getName())
                .tags(post.getTags())
                .url(post.getUrl())
                .build();
    }

    private Post convert(PostEntity postEntity) {
        return Post.builder()
                .id(postEntity.getId())
                .description(postEntity.getDescription())
                .lastDatePublished(postEntity.getLastDatePublished())
                .name(postEntity.getName())
                .tags(postEntity.getTags())
                .url(postEntity.getUrl()).build();
    }
}