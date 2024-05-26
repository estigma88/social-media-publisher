package com.coderstower.socialmediapubisher.application.factory;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MailProperties {
    private final String senderEmail;
    private final String receiverEmail;
}
