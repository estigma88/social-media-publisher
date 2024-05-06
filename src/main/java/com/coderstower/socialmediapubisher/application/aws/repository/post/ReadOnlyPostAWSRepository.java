package com.coderstower.socialmediapubisher.application.aws.repository.post;

import com.coderstower.socialmediapubisher.domain.post.repository.Post;
import com.coderstower.socialmediapubisher.domain.post.repository.PostRepository;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class ReadOnlyPostAWSRepository extends PostAWSRepository {
    public ReadOnlyPostAWSRepository(PostDynamoRepository postDynamoRepository) {
        super(postDynamoRepository);
    }

    @Override
    public Post update(Post post) {
        return post;
    }
}
