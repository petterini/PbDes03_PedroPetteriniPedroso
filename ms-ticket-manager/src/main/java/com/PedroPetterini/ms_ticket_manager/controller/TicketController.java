package com.PedroPetterini.ms_ticket_manager.controller;

import com.PedroPetterini.ms_ticket_manager.dto.ErrorResponseDto;
import com.PedroPetterini.ms_ticket_manager.dto.TicketResponseDto;
import com.PedroPetterini.ms_ticket_manager.exception.EventNotFoundException;
import com.PedroPetterini.ms_ticket_manager.exception.TicketNotFoundException;
import com.PedroPetterini.ms_ticket_manager.model.Ticket;
import com.PedroPetterini.ms_ticket_manager.service.EventService;
import com.PedroPetterini.ms_ticket_manager.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("ticketManagement/v1")
public class TicketController {

    private final TicketService ticketService;
    private final EventService eventService;

    @PostMapping
    public ResponseEntity<Object> createTicket(@Valid @RequestBody Ticket ticket) {
        try {
            ticketService.createTicket(ticket);
            TicketResponseDto ticketResponseDto = eventService.toDto(ticket);
            return ResponseEntity.status(HttpStatus.CREATED).body(ticketResponseDto);
        } catch (EventNotFoundException e) {
            var errorMessage = ErrorResponseDto.eventNotFoundResponse(e.getMessage());
            return ResponseEntity.status(errorMessage.status()).body(errorMessage);
        }

    }

    @GetMapping("/get-all-tickets")
    public ResponseEntity<List<TicketResponseDto>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getTicketById(@PathVariable("id") String id) {
        try {
            return ResponseEntity.ok(ticketService.getTicketById(id));
        } catch (TicketNotFoundException e) {
            var errorMessage = ErrorResponseDto.ticketNotFoundResponse(e.getMessage());
            return ResponseEntity.status(errorMessage.status()).body(errorMessage);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTicketById(@PathVariable("id") String id) {
        try {
            ticketService.softDeleteTicketById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (TicketNotFoundException e) {
            var errorMessage = ErrorResponseDto.ticketNotFoundResponse(e.getMessage());
            return ResponseEntity.status(errorMessage.status()).body(errorMessage);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateTicketById(@PathVariable("id") String id, @Valid @RequestBody Ticket ticket) {
        try {
            return ResponseEntity.ok(ticketService.updateTicket(id, ticket));
        } catch (TicketNotFoundException e) {
            var errorMessage = ErrorResponseDto.ticketNotFoundResponse(e.getMessage());
            return ResponseEntity.status(errorMessage.status()).body(errorMessage);
        }catch (EventNotFoundException e) {
            var errorMessage = ErrorResponseDto.eventNotFoundResponse(e.getMessage());
            return ResponseEntity.status(errorMessage.status()).body(errorMessage);
        }
    }

    @GetMapping("/check-tickets-by-event/{eventId}")
    public ResponseEntity<Object> checkTicketsByEvent(@PathVariable("eventId") String eventId) {
        try {
            return ResponseEntity.ok(ticketService.findByEventId(eventId));
        } catch (TicketNotFoundException e) {
            var errorMessage = ErrorResponseDto.ticketNotFoundResponse(e.getMessage());
            return ResponseEntity.status(errorMessage.status()).body(errorMessage);
        }
    }
}
