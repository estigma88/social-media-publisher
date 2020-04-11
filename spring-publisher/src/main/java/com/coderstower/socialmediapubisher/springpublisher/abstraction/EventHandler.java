package com.coderstower.socialmediapubisher.springpublisher.abstraction;

import com.google.common.eventbus.Subscribe;

@FunctionalInterface
public interface EventHandler<T> {
    @Subscribe
    void handle(T t);
}
