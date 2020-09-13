package com.coderstower.socialmediapubisher.springpublisher.main.aws.repository.oauth2;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedJson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

@Data
@DynamoDBTable(tableName = "OAuth2AuthorizationRequest")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2AuthorizationRequestDynamo {
    @DynamoDBHashKey
    private String id;
    @DynamoDBAttribute
    @DynamoDBTypeConverted(converter = OAuth2AuthorizationRequestDynamoDBTypeConverter.class)
    private OAuth2AuthorizationRequest authorizationRequest;
}
