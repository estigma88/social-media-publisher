package com.coderstower.socialmediapubisher.springpublisher.main.aws.repository;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

class URLConverterTest {
    private URLConverter urlConverter = new URLConverter();

    @Test
    public void convert() throws MalformedURLException {
        String url = urlConverter.convert(URI.create("https://coderstower.com/2020/01/13/open-close-principle-by-example/").toURL());

        assertThat(url).isEqualTo("https://coderstower.com/2020/01/13/open-close-principle-by-example/");
    }

    @Test
    public void unconvert() throws MalformedURLException {
        URL url = urlConverter.unconvert("https://coderstower.com/2020/01/13/open-close-principle-by-example/");

        assertThat(url).isEqualTo(URI.create("https://coderstower.com/2020/01/13/open-close-principle-by-example/").toURL());
    }

}