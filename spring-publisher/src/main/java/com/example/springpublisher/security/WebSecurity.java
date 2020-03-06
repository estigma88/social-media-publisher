package com.example.springpublisher.security;

import com.coderstower.blog.oidc_spring_security.client.controller.RedirectFilter;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;

@EnableWebSecurity
public class WebSecurity extends
        WebSecurityConfigurerAdapter {
  @Override
  protected void configure(HttpSecurity http)
          throws Exception {
    http.authorizeRequests(
            (requests) -> requests.anyRequest()
                    .authenticated());
    http.oauth2Login(Customizer.withDefaults());
    http.oauth2Client();

   http
            .addFilterBefore(new RedirectFilter(),
                    OAuth2AuthorizationRequestRedirectFilter.class);
  }
}
