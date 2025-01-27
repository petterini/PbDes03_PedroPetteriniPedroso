package com.PedroPetterini.ms_ticket_manager.consumer;

import com.PedroPetterini.ms_ticket_manager.model.Email;
import com.PedroPetterini.ms_ticket_manager.service.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class EmailConsumer {

    @Autowired
    EmailService emailService;

    @RabbitListener(queues= "${spring.rabbitmq.queue}")
    public void listen(@Payload Email email) {
        Email e = new Email();
        emailService.sendEmail(email);
    }
}
