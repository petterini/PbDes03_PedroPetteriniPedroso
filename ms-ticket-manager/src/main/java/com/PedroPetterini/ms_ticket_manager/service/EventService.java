package com.PedroPetterini.ms_ticket_manager.service;

import com.PedroPetterini.ms_ticket_manager.consumer.EventConsumer;
import com.PedroPetterini.ms_ticket_manager.dto.TicketResponseDto;
import com.PedroPetterini.ms_ticket_manager.dto.mapper.TicketMapper;
import com.PedroPetterini.ms_ticket_manager.model.Event;
import com.PedroPetterini.ms_ticket_manager.model.Ticket;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public List<TicketResponseDto> toDto(List<Ticket> tickets) {
        List<TicketResponseDto> ticketResponseDtos = new ArrayList<>();
        for (Ticket ticket : tickets) {
            if (ticket.getActive()) {
                Event event = eventConsumer.getEvent(ticket.getEventId());
                TicketResponseDto ticketResponseDto = ticketMapper.toDto(ticket);
                ticketResponseDto.setEvent(event);
                ticketResponseDtos.add(ticketResponseDto);
            }
        }
        return ticketResponseDtos;
    }
}
