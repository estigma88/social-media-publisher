package com.coderstower.socialmediapubisher.springpublisher.main.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.util.UriTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Paths;

public class RedirectAwareAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UriTemplate loginURL;

    public RedirectAwareAuthenticationSuccessHandler(UriTemplate loginURL) {
        this.loginURL = loginURL;
    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        this.clearAuthenticationAttributes(request);
        String socialMediaId = Paths.get(request.getServletPath()).getFileName().toString();
        String targetUrl = loginURL.expand(socialMediaId).toString();
//        this.getRedirectStrategy().sendRedirect(request, response, targetUrl);
        request.getRequestDispatcher("/oauth2/linkedin/credentials").forward(request, response);
//        super.onAuthenticationSuccess(request, response, authentication);
    }
}
