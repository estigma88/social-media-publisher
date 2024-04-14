package com.coderstower.socialmediapubisher.springpublisher.main.aws.repository;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class LocalDateTimeConverterTest {
    private LocalDateTimeConverter localDateTimeConverter = new LocalDateTimeConverter();

    @Test
    public void convert(){
        String date = localDateTimeConverter.convert(LocalDateTime.of(2020, 3, 3, 5, 6, 8, 1));

        assertThat(date).isEqualTo("2020-03-03T05:06:08.000000001");
    }

    @Test
    public void unconvert(){
        LocalDateTime date = localDateTimeConverter.unconvert("2020-03-03T05:06:08.000000001");

        assertThat(date).isEqualTo(LocalDateTime.of(2020, 3, 3, 5, 6, 8, 1));
    }

}