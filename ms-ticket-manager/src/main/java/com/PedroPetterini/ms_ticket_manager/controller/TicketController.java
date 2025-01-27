package com.PedroPetterini.ms_ticket_manager.controller;

import com.PedroPetterini.ms_ticket_manager.dto.TicketResponseDto;
import com.PedroPetterini.ms_ticket_manager.model.Ticket;
import com.PedroPetterini.ms_ticket_manager.service.EventService;
import com.PedroPetterini.ms_ticket_manager.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/get-all-tickets")
    public ResponseEntity<List<TicketResponseDto>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDto> getTicketById(@PathVariable("id") String id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicketById(@PathVariable("id") String id) {
        ticketService.softDeleteTicketById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketResponseDto> updateTicketById(@PathVariable("id") String id, @RequestBody Ticket ticket) {
        return ResponseEntity.ok(ticketService.updateTicket(id, ticket));
    }

    @GetMapping("/check-tickets-by-event/{eventId}")
    public ResponseEntity<List<TicketResponseDto>> checkTicketsByEvent(@PathVariable("eventId") String eventId) {
        return ResponseEntity.ok(ticketService.findByEventId(eventId));
    }
}
