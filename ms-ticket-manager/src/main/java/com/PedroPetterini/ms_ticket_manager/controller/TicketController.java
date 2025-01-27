package com.PedroPetterini.ms_ticket_manager.controller;

import com.PedroPetterini.ms_ticket_manager.dto.TicketResponseDto;
import com.PedroPetterini.ms_ticket_manager.model.Ticket;
import com.PedroPetterini.ms_ticket_manager.service.EventService;
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
    private final EventService eventService;

    public TicketController(TicketService ticketService, EventService eventService) {
        this.ticketService = ticketService;
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<TicketResponseDto> createTicket(@RequestBody Ticket ticket) {
        ticketService.createTicket(ticket);
        TicketResponseDto ticketResponseDto = eventService.toDto(ticket);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketResponseDto);
    }
}
