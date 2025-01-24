package com.PedroPetterini.ms_ticket_manager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor @NoArgsConstructor
@Data
@Document
public class Ticket {

    @Id
    private String ticketId;
    private String customerName;
    private String cpf;
    private String customerMail;
    private String eventId;
    private String eventName;
    private Double brlAmount;
    private Double usdAmount;
}
