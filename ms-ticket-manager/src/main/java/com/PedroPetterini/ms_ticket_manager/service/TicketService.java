package com.PedroPetterini.ms_ticket_manager.service;

import com.PedroPetterini.ms_ticket_manager.consumer.EventConsumer;
import com.PedroPetterini.ms_ticket_manager.model.Ticket;
import com.PedroPetterini.ms_ticket_manager.repository.TicketRepository;
import feign.Response;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final EventConsumer eventConsumer;

    public TicketService(TicketRepository ticketRepository, EventConsumer eventConsumer) {
        this.ticketRepository = ticketRepository;
        this.eventConsumer = eventConsumer;
    }

    public Ticket createTicket(Ticket ticket) {
        Response response = eventConsumer.getEventResponse(ticket.getEventId());
        System.out.println(response.status());

        if(response.status() == 200) {
            return ticketRepository.save(ticket);
        }else{
            throw new RuntimeException("Error creating ticket");
        }
    }
}
