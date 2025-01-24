package com.PedroPetterini.ms_ticket_manager.controller;

import com.PedroPetterini.ms_ticket_manager.model.Ticket;
import com.PedroPetterini.ms_ticket_manager.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("ticketManagement/v1")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody Ticket ticket) {
        ticketService.createTicket(ticket);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
    }
}
