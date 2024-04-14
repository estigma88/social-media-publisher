package com.coderstower.socialmediapubisher.springpublisher.abstraction.security;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}
