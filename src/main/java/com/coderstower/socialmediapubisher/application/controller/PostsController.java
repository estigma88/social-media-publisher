package com.coderstower.socialmediapubisher.application.controller;


import com.coderstower.socialmediapubisher.domain.post.PostPublisher;
import com.coderstower.socialmediapubisher.domain.post.repository.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
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

    @RequestMapping(path = "/posts/{group}/next", method = RequestMethod.POST)
    public Post postNext(@PathVariable String group) {
        Post post = postPublisher.publishNext(group);

        log.info("Published Post from group {}: {}", group, post);

        return post;
    }
}
