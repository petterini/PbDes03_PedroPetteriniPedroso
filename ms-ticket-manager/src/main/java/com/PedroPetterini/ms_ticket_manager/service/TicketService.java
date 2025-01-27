package com.PedroPetterini.ms_ticket_manager.service;

import com.PedroPetterini.ms_ticket_manager.consumer.EventConsumer;
import com.PedroPetterini.ms_ticket_manager.dto.TicketResponseDto;
import com.PedroPetterini.ms_ticket_manager.model.Ticket;
import com.PedroPetterini.ms_ticket_manager.repository.TicketRepository;
import feign.Response;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final EventConsumer eventConsumer;
    private final EventService eventService;

    public TicketService(TicketRepository ticketRepository, EventConsumer eventConsumer, EventService eventService) {
        this.ticketRepository = ticketRepository;
        this.eventConsumer = eventConsumer;
        this.eventService = eventService;
    }

    public Ticket createTicket(Ticket ticket) {
        Response response = eventConsumer.getEventResponse(ticket.getEventId());
        if (response.status() == 200) {
            return ticketRepository.save(ticket);
        } else {
            throw new RuntimeException("Error creating ticket");
        }
    }

    public List<TicketResponseDto> getAllTickets() {
        List<Ticket> tickets = ticketRepository.findAll();
        return eventService.toDto(tickets);
    }

    public TicketResponseDto getTicketById(String ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        return eventService.toDto(ticket);
    }

    public void softDeleteTicketById(String ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        ticket.setActive(false);
        ticketRepository.save(ticket);
    }

//    public TicketResponseDto updateTicket(String id, Ticket ticket) {
//        try {
//            Ticket ticketToUpdate = ticketRepository.findById(id).orElse(null);
//            updateData(ticketToUpdate, ticket);
//            ticketRepository.save(ticketToUpdate);
//            return eventService.toDto(ticketToUpdate);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    public TicketResponseDto updateTicket(String id, Ticket ticket) {
        try {
            Ticket ticketToUpdate = ticketRepository.findById(id).orElse(null);
            updateData(ticketToUpdate, ticket);
            ticketRepository.save(ticketToUpdate);
            return eventService.toDto(ticketToUpdate);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void updateData(Ticket ticketToUpdate, Ticket ticket) {
        ticketToUpdate.setEventId(ticket.getEventId());
        ticketToUpdate.setEventName(ticket.getEventName());
        ticketToUpdate.setCpf(ticket.getCpf());
        ticketToUpdate.setCustomerName(ticket.getCustomerName());
        ticketToUpdate.setCustomerMail(ticket.getCustomerMail());
        ticketToUpdate.setBrlAmount(ticket.getBrlAmount());
        ticketToUpdate.setUsdAmount(ticket.getUsdAmount());
    }
}
