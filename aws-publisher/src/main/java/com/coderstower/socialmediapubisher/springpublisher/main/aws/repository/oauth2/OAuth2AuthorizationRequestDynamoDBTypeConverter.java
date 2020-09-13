package com.coderstower.socialmediapubisher.springpublisher.main.aws.repository.oauth2;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMappingException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class OAuth2AuthorizationRequestDynamoDBTypeConverter implements DynamoDBTypeConverter<String, OAuth2AuthorizationRequest> {
    private static final ObjectMapper mapper;

    public final String convert(OAuth2AuthorizationRequest object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception var3) {
            throw new DynamoDBMappingException("Unable to write object to JSON", var3);
        }
    }

    public final OAuth2AuthorizationRequest unconvert(String object) {
        try {
            Map<String, Object> properties = mapper.readValue(object, new TypeReference<>() {
            });

            return OAuth2AuthorizationRequest
                    .authorizationCode()
                    .additionalParameters((Map<String, Object>) properties.get("additionalParameters"))
                    .attributes((Map<String, Object>) properties.get("attributes"))
                    .authorizationRequestUri((String) properties.get("authorizationRequestUri"))
                    .authorizationUri((String) properties.get("authorizationUri"))
                    .clientId((String) properties.get("clientId"))
                    .redirectUri((String) properties.get("redirectUri"))
                    .scopes((new HashSet<>((Collection<String>) properties.get("scopes"))))
                    .state((String) properties.get("state"))
                    .build();
        } catch (Exception var3) {
            throw new DynamoDBMappingException("Unable to read JSON string", var3);
        }
    }

    static {
        mapper = (new ObjectMapper()).disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }
}
