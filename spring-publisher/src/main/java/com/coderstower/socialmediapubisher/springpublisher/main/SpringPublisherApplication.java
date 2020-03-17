package com.coderstower.socialmediapubisher.springpublisher.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class SpringPublisherApplication {

    public static void main(String[] args) {
        SpringApplication
                .run(SpringPublisherApplication.class,
                        args);
    }

}
