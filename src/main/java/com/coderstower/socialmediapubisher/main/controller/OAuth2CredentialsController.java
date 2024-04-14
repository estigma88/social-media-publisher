package com.coderstower.socialmediapubisher.main.controller;


import com.coderstower.socialmediapubisher.abstraction.security.OAuth2CredentialsManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class OAuth2CredentialsController {
    private final OAuth2CredentialsManager oAuth2CredentialsManager;

    public OAuth2CredentialsController(OAuth2CredentialsManager oAuth2CredentialsManager) {
        this.oAuth2CredentialsManager = oAuth2CredentialsManager;
    }

    /**
     * Endpoint secured to redirect to the right authorization provider.
     * Needs to be a GET to allow access from the browser and the whole 0Auth2 flow
     */
    @RequestMapping(path = "/oauth2/{socialAccount}/credentials", method = RequestMethod.GET)
    public ResponseEntity<String> updateOAuth2Credentials(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient, @PathVariable String socialAccount) {
        oAuth2CredentialsManager.update(authorizedClient, socialAccount);

        return ResponseEntity.ok("Credentials updated");
    }
}
