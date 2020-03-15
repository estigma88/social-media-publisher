package com.coderstower.socialmediapubisher.springpublisher.main.post.repository.credential;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Oauth1CredentialEntity {
    @Id
    private String id;
    private String consumerKey;
    private String consumerSecret;
    private String accessToken;
    private String tokenSecret;
}
