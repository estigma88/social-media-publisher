package com.coderstower.socialmediapubisher.springpublisher.main.controller;


import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.PostPublisher;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class PostsController {
    private final PostPublisher postPublisher;

    public PostsController(PostPublisher postPublisher) {
        this.postPublisher = postPublisher;
    }

    @RequestMapping(path = "/ping", method = RequestMethod.GET)
    public Map<String, String> ping() {
        Map<String, String> pong = new HashMap<>();
        pong.put("pong", "Hello, World!");

        log.info("Pong: {}", pong);

        return pong;
    }

    @RequestMapping(path = "/posts/next", method = RequestMethod.POST)
    public Post postNext() {
        Post post = postPublisher.publishNext();

        log.info("Published Post: {}", post);

        return post;
    }
}
