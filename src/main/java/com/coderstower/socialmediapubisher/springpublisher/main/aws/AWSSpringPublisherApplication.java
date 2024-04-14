package com.coderstower.socialmediapubisher.springpublisher.main.aws;

import com.coderstower.socialmediapubisher.springpublisher.main.controller.ErrorHandler;
import com.coderstower.socialmediapubisher.springpublisher.main.controller.OAuth2CredentialsController;
import com.coderstower.socialmediapubisher.springpublisher.main.controller.PostsController;
import com.coderstower.socialmediapubisher.springpublisher.main.factory.SecurityFactory;
import com.coderstower.socialmediapubisher.springpublisher.main.factory.SpringPublisherFactory;
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

    /*
     * Create required HandlerMapping, to avoid several default HandlerMapping instances being created

    @Bean
    public HandlerMapping handlerMapping() {
        return new RequestMappingHandlerMapping();
    }*/

    /*
     * Create required HandlerAdapter, to avoid several default HandlerAdapter instances being created

    @Bean
    public HandlerAdapter handlerAdapter() {
        return new RequestMappingHandlerAdapter();
    }*/

    /*
     * optimization - avoids creating default exception resolvers; not required as the serverless container handles
     * all exceptions
     *
     * By default, an ExceptionHandlerExceptionResolver is created which creates many dependent object, including
     * an expensive ObjectMapper instance.
     *
     * To enable custom @ControllerAdvice classes remove this bean.
     */
    /*@Bean
    public HandlerExceptionResolver handlerExceptionResolver() {
        return new HandlerExceptionResolver() {

            @Override
            public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
                return null;
            }
        };
    }*/

    public static void main(String[] args) {
        SpringApplication.run(AWSSpringPublisherApplication.class, args);
    }
}