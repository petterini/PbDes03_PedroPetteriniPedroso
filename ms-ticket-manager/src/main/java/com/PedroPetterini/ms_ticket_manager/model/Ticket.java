package com.PedroPetterini.ms_ticket_manager.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor @NoArgsConstructor
@Data
@Document
public class Ticket {

    @Id
    private String ticketId;

    @NotBlank(message = "Customer name is required.")
    private String customerName;

    @NotBlank(message = "CPF is required.")
    @CPF(message = "Invalid CPF format.")
    private String cpf;

    @NotBlank(message = "Customer email is required.")
    @Email(message = "Invalid email format.")
    private String customerMail;

    @NotBlank(message = "Event ID is required.")
    private String eventId;

    @NotBlank(message = "Event name is required.")
    private String eventName;

    private Double brlAmount;
    private Double usdAmount;
    private Boolean active = true;
}
