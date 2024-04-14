package com.coderstower.socialmediapubisher.main.aws.repository.post;

import com.coderstower.socialmediapubisher.domain.post.repository.Post;
import com.coderstower.socialmediapubisher.domain.post.repository.PostRepository;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class PostAWSRepository implements PostRepository {
    private final PostDynamoRepository postDynamoRepository;

    public PostAWSRepository(PostDynamoRepository postDynamoRepository) {
        this.postDynamoRepository = postDynamoRepository;
    }

    @Override
    public Optional<Post> getNextToPublish(String group) {
        return StreamSupport.stream(postDynamoRepository.findAll().spliterator(), false)
                .filter(postDynamo -> group.equals(postDynamo.getGroup()))
                .min(Comparator.comparing(PostDynamo::getPublishedDate))
                .map(this::convert);
    }

    @Override
    public Post update(Post post) {
        return convert(postDynamoRepository.save(convert(post)));
    }

    private PostDynamo convert(Post post) {
        return PostDynamo.builder()
                .id(post.getId())
                .description(post.getDescription())
                .publishedDate(post.getPublishedDate())
                .name(post.getName())
                .tags(post.getTags())
                .url(post.getUrl())
                .group(post.getGroup())
                .build();
    }

    private Post convert(PostDynamo postDynamo) {
        return Post.builder()
                .id(postDynamo.getId())
                .description(postDynamo.getDescription())
                .publishedDate(postDynamo.getPublishedDate())
                .name(postDynamo.getName())
                .tags(postDynamo.getTags())
                .group(postDynamo.getGroup())
                .url(postDynamo.getUrl()).build();
    }
}
