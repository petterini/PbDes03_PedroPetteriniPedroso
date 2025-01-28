package com.PedroPetterini.ms_ticket_manager.service;

import com.PedroPetterini.ms_ticket_manager.consumer.EmailConsumer;
import com.PedroPetterini.ms_ticket_manager.consumer.EventConsumer;
import com.PedroPetterini.ms_ticket_manager.dto.TicketResponseDto;
import com.PedroPetterini.ms_ticket_manager.dto.mapper.TicketMapper;
import com.PedroPetterini.ms_ticket_manager.exception.EventNotFoundException;
import com.PedroPetterini.ms_ticket_manager.exception.TicketNotFoundException;
import com.PedroPetterini.ms_ticket_manager.model.Email;
import com.PedroPetterini.ms_ticket_manager.model.Ticket;
import com.PedroPetterini.ms_ticket_manager.repository.TicketRepository;
import feign.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final EventConsumer eventConsumer;
    private final EventService eventService;
    private final EmailService emailService;


    public Ticket createTicket(Ticket ticket) {
        Response response = eventConsumer.getEventResponse(ticket.getEventId());
        if (response.status() == 200) {
            sendMailTicketConfirmation(ticket);
            return ticketRepository.save(ticket);
        } else {
            throw new EventNotFoundException("Error creating ticket, event not found");
        }
    }


    public List<TicketResponseDto> getAllTickets() {
        List<Ticket> tickets = ticketRepository.findAll();
        return eventService.toDto(tickets);
    }

    public TicketResponseDto getTicketById(String ticketId) {
        if (ticketRepository.existsById(ticketId)) {
            return eventService.toDto(ticketRepository.findById(ticketId).get());
        }
        throw new TicketNotFoundException("Ticket not found");
    }

    public void softDeleteTicketById(String ticketId) {
        if (ticketRepository.existsById(ticketId)) {
            Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
            ticket.setActive(false);
            ticketRepository.save(ticket);
        }else {
            throw new TicketNotFoundException("Ticket not found");
        }
    }

    public List<TicketResponseDto> findByEventId(String eventId) {
        List<Ticket> tickets = ticketRepository.findByEventId(eventId);
        if (!tickets.isEmpty()) {
            return eventService.toDto(tickets);
        }
        throw new TicketNotFoundException("Ticket not found");
    }

    public TicketResponseDto updateTicket(String id, Ticket ticket) {
        if(ticketRepository.existsById(id)) {
            Ticket ticketToUpdate = ticketRepository.findById(id).get();
            updateData(ticketToUpdate, ticket);
            ticketRepository.save(ticketToUpdate);
            sendMailTicketAlteration(ticket);
            return eventService.toDto(ticketToUpdate);
        }
        throw new TicketNotFoundException("Ticket not found");
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

    private void sendMailTicketConfirmation(Ticket ticket) {
        Email email = new Email();
        email.setEmailTo(ticket.getCustomerMail());
        email.setSubject("Ticket Confirmation from: " + ticket.getEventName());
        email.setBody("Hello, " + ticket.getCustomerName() + ", ticket Confirmation from: " + ticket.getEventName());
        emailService.sendEmail(email);
    }

    private void sendMailTicketAlteration(Ticket ticket) {
        Email email = new Email();
        email.setEmailTo(ticket.getCustomerMail());
        email.setSubject("Ticket Alteration from: " + ticket.getEventName());
        email.setBody("Hello, " + ticket.getCustomerName() + ", ticket alteration from: " + ticket.getEventName());
        emailService.sendEmail(email);

    }
}
