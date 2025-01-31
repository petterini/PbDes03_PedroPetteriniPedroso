package com.PedroPetterini.ms_ticket_manager.service;

import com.PedroPetterini.ms_ticket_manager.model.Email;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendEmail() {
        Email email = new Email();
        email.setEmailFrom("sender@example.com");
        email.setEmailTo("recipient@example.com");
        email.setSubject("Test Subject");
        email.setBody("This is a test email body.");

        emailService.sendEmail(email);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}