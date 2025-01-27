package com.PedroPetterini.ms_ticket_manager.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

@Getter
@Setter
public class Email {

    @Value("${spring.mail.username}")
    private String emailFrom;

    private String emailTo;
    private String subject;
    private String body;

    public Email() {
    }

    public Email(String emailTo, String subject, String body) {
        this.emailTo = emailTo;
        this.subject = subject;
        this.body = body;
    }

}
