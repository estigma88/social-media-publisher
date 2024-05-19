package com.coderstower.socialmediapubisher.application.aws.repository.oauth1;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@DynamoDBTable(tableName = "Oauth1Credentials")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuth1CredentialDynamo {
    @DynamoDBHashKey
    private String id;
    @DynamoDBAttribute
    private String consumerKey;
    @DynamoDBAttribute
    private String consumerSecret;
    @DynamoDBAttribute
    private String accessToken;
    @DynamoDBAttribute
    private String tokenSecret;
}
