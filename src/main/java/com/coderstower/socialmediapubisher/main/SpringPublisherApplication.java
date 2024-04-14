package com.coderstower.socialmediapubisher.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class SpringPublisherApplication {

    public static void main(String[] args) {
        SpringApplication
                .run(SpringPublisherApplication.class,
                        args);
    }

}
