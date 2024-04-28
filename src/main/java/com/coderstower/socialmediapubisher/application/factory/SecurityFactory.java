package com.coderstower.socialmediapubisher.application.factory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityFactory {
    /**
     * Creating individual security by social network. If we generalize,
     * Spring Security shows a list of social networks to login with.
     */
//    @Order(1)
//    @Profile("secure")
//    @Configuration
//    public static class LinkedinSecurity extends WebSecurityConfigurerAdapter {
//        private final ClientRegistrationRepository clientRegistrationRepository;
//
//        public LinkedinSecurity(ClientRegistrationRepository clientRegistrationRepository) {
//            this.clientRegistrationRepository = clientRegistrationRepository;
//        }
//
//        @Override
//        protected void configure(HttpSecurity http) throws Exception {
//            http
//                    .authorizeRequests()
//                    .antMatchers("/oauth2/linkedin/credentials").authenticated()
//                    .and()
//                    .oauth2Login()
//                    /*
//                    Create a new ClientRegistrationRepository with only Linkedin configuration to avoid
//                    using other OAuth2 configuration over this endpoint
//                     */
//                    .clientRegistrationRepository(new InMemoryClientRegistrationRepository(clientRegistrationRepository.findByRegistrationId("linkedin")))
//                    .tokenEndpoint()
//                    .accessTokenResponseClient(authorizationCodeTokenResponseClient());
//        }
//
//        private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> authorizationCodeTokenResponseClient() {
//            OAuth2AccessTokenResponseHttpMessageConverter tokenResponseHttpMessageConverter =
//                    new OAuth2AccessTokenResponseHttpMessageConverter();
//            tokenResponseHttpMessageConverter.setTokenResponseConverter(new OAuth2AccessTokenResponseConverterWithDefaults());
//
//            RestTemplate restTemplate = new RestTemplate(Arrays.asList(
//                    new FormHttpMessageConverter(), tokenResponseHttpMessageConverter));
//            restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
//
//            DefaultAuthorizationCodeTokenResponseClient tokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
//            tokenResponseClient.setRestOperations(restTemplate);
//
//            return tokenResponseClient;
//        }
//    }
//
//    @Order(2)
//    @Configuration
//    public static class NoSecurity extends WebSecurityConfigurerAdapter {
//        @Override
//        protected void configure(HttpSecurity http) throws Exception {
//            http
//                    .csrf().disable()
//                    .authorizeRequests()
//                    .antMatchers("/**")
//                    .permitAll();
//        }
//    }
    @Bean
    public SecurityFilterChain noSecurity(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .securityMatcher("/**")
                .authorizeHttpRequests(authorize ->
                        authorize
                                .anyRequest()
                                .permitAll()
                )
                .build();
    }
}
