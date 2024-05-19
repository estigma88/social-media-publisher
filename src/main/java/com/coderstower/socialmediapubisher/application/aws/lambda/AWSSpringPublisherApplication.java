package com.coderstower.socialmediapubisher.application.aws.lambda;

import com.coderstower.socialmediapubisher.application.aws.repository.SpringPublisherDynamoDBRepositoryFactory;
import com.coderstower.socialmediapubisher.application.controller.OAuth2CredentialsController;
import com.coderstower.socialmediapubisher.application.factory.SecurityFactory;
import com.coderstower.socialmediapubisher.application.controller.ErrorHandler;
import com.coderstower.socialmediapubisher.application.controller.PostsController;
import com.coderstower.socialmediapubisher.application.factory.SpringPublisherFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;


@SpringBootApplication
// We use direct @Import instead of @ComponentScan to speed up cold starts
// @ComponentScan(basePackages = "my.service.controller")
@Import({PostsController.class, OAuth2CredentialsController.class, SecurityFactory.class,
        SpringPublisherFactory.class, SpringPublisherDynamoDBRepositoryFactory.class, ErrorHandler.class})
public class AWSSpringPublisherApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(AWSSpringPublisherApplication.class, args);
    }
}