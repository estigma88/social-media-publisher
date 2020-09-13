package com.coderstower.socialmediapubisher.springpublisher.main.aws.repository.oauth2;

import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class OAuth2AuthorizationRequestAWSRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    private final OAuth2AuthorizationRequestDynamoRepository oAuth2AuthorizationRequestDynamoRepository;

    public OAuth2AuthorizationRequestAWSRepository(OAuth2AuthorizationRequestDynamoRepository oAuth2AuthorizationRequestDynamoRepository) {
        this.oAuth2AuthorizationRequestDynamoRepository = oAuth2AuthorizationRequestDynamoRepository;
    }

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        Assert.notNull(request, "request cannot be null");
        String stateParameter = this.getStateParameter(request);
        if (stateParameter == null) {
            return null;
        } else {
            return oAuth2AuthorizationRequestDynamoRepository.findById(stateParameter)
                    .map(OAuth2AuthorizationRequestDynamo::getAuthorizationRequest)
                    .orElse(null);
        }
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        Assert.notNull(request, "request cannot be null");
        Assert.notNull(response, "response cannot be null");
        if (authorizationRequest == null) {
            this.removeAuthorizationRequest(request, response);
        } else {
            String state = authorizationRequest.getState();
            Assert.hasText(state, "authorizationRequest.state cannot be empty");
            oAuth2AuthorizationRequestDynamoRepository.save(OAuth2AuthorizationRequestDynamo.builder()
                    .id(state)
                    .authorizationRequest(authorizationRequest)
                    .build());
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
        Assert.notNull(request, "request cannot be null");
        String stateParameter = this.getStateParameter(request);
        if (stateParameter == null) {
            return null;
        } else {
            Optional<OAuth2AuthorizationRequestDynamo> originalRequest = oAuth2AuthorizationRequestDynamoRepository.findById(stateParameter);
            originalRequest.ifPresent(oAuth2AuthorizationRequestDynamoRepository::delete);
            return originalRequest
                    .map(OAuth2AuthorizationRequestDynamo::getAuthorizationRequest)
                    .orElse(null);
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        Assert.notNull(response, "response cannot be null");
        return this.removeAuthorizationRequest(request);
    }

    private String getStateParameter(HttpServletRequest request) {
        return request.getParameter("state");
    }
}
