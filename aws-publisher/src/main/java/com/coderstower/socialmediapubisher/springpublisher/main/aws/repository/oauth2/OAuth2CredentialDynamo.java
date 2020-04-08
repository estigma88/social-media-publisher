package com.coderstower.socialmediapubisher.springpublisher.main.aws.repository.oauth2;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.coderstower.socialmediapubisher.springpublisher.main.aws.repository.LocalDateTimeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@DynamoDBTable(tableName = "OAuth2Credentials")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2CredentialDynamo {
    @DynamoDBHashKey
    private String id;
    @DynamoDBAttribute
    private String accessToken;
    @DynamoDBAttribute
    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    private LocalDateTime expirationDate;
}
