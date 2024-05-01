package com.coderstower.socialmediapubisher.extesion;

import com.github.tomakehurst.wiremock.common.Notifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpringSlf4jNotifier implements Notifier {
    private static final Logger log = LoggerFactory.getLogger(SpringSlf4jNotifier.class);

    public void info(String message) {
        log.info(message);
    }

    public void error(String message) {
        log.error(message);
    }

    public void error(String message, Throwable t) {
        log.error(message, t);
    }
}
