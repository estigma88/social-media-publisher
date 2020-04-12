package com.coderstower.socialmediapubisher.springpublisher.main.security;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.security.OAuth2AccessTokenResponseConverterWithDefaults;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Order(1)
    @Configuration
    public static class LinkedinSecurity extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    .antMatchers("/oauth2/linkedin/credentials").authenticated()
                    .and()
                    .oauth2Login()
                    .tokenEndpoint()
                    .accessTokenResponseClient(authorizationCodeTokenResponseClient());
        }

        private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> authorizationCodeTokenResponseClient() {
            OAuth2AccessTokenResponseHttpMessageConverter tokenResponseHttpMessageConverter =
                    new OAuth2AccessTokenResponseHttpMessageConverter();
            tokenResponseHttpMessageConverter.setTokenResponseConverter(new OAuth2AccessTokenResponseConverterWithDefaults());

            RestTemplate restTemplate = new RestTemplate(Arrays.asList(
                    new FormHttpMessageConverter(), tokenResponseHttpMessageConverter));
            restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());

            DefaultAuthorizationCodeTokenResponseClient tokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
            tokenResponseClient.setRestOperations(restTemplate);

            return tokenResponseClient;
        }
    }

    @Order(2)
    @Configuration
    public static class NoSecurity extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    .antMatchers("/ping", "/posts/next").permitAll();
        }
    }
}
