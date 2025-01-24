package com.PedroPetterini.ms_ticket_manager.dto;

import com.PedroPetterini.ms_ticket_manager.model.Event;


public class TicketResponseDto {
    private String ticketId;
    private String customerName;
    private String cpf;
    private String customerMail;
    private Event event;
    private Double brlAmount;
    private Double usdAmount;
}
