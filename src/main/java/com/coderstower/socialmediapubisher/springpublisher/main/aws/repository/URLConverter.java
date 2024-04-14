package com.coderstower.socialmediapubisher.springpublisher.main.aws.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class URLConverter implements DynamoDBTypeConverter<String, URL> {
    @Override
    public String convert(final URL url) {
        return url.toString();
    }

    @Override
    public URL unconvert(final String stringValue) {
        try {
            return URI.create(stringValue).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
