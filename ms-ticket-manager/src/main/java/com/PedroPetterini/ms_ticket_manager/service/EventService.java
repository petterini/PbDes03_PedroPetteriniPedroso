package com.PedroPetterini.ms_ticket_manager.service;

import com.PedroPetterini.ms_ticket_manager.consumer.EventConsumer;
import com.PedroPetterini.ms_ticket_manager.dto.TicketResponseDto;
import com.PedroPetterini.ms_ticket_manager.dto.mapper.TicketMapper;
import com.PedroPetterini.ms_ticket_manager.model.Event;
import com.PedroPetterini.ms_ticket_manager.model.Ticket;
import org.springframework.stereotype.Service;

@Service
public class EventService {

    private final TicketMapper ticketMapper;
    private final EventConsumer eventConsumer;

    public EventService(TicketMapper ticketMapper, EventConsumer eventConsumer) {
        this.ticketMapper = ticketMapper;
        this.eventConsumer = eventConsumer;
    }

    public TicketResponseDto toDto(Ticket ticket) {
        Event event = eventConsumer.getEvent(ticket.getEventId());
        TicketResponseDto ticketResponseDto = ticketMapper.toDto(ticket);
        ticketResponseDto.setEvent(event);
        return ticketResponseDto;
    }
}
