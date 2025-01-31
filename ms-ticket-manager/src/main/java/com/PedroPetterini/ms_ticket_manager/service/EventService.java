package com.PedroPetterini.ms_ticket_manager.service;

import com.PedroPetterini.ms_ticket_manager.consumer.EventConsumer;
import com.PedroPetterini.ms_ticket_manager.dto.TicketResponseDto;
import com.PedroPetterini.ms_ticket_manager.dto.mapper.TicketMapper;
import com.PedroPetterini.ms_ticket_manager.exception.EventNotFoundException;
import com.PedroPetterini.ms_ticket_manager.model.Event;
import com.PedroPetterini.ms_ticket_manager.model.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final TicketMapper ticketMapper;
    private final EventConsumer eventConsumer;

    public TicketResponseDto toDto(Ticket ticket) {
        Event event = null;

        if (eventConsumer.getEventResponse(ticket.getEventId()).status() != HttpStatus.NOT_FOUND.value()) {
            event = eventConsumer.getEvent(ticket.getEventId());
        }

        if (event == null && eventConsumer.getEventResponseByName(ticket.getEventName()).status() != HttpStatus.NOT_FOUND.value()) {
            event = eventConsumer.getEventByName(ticket.getEventName());
        }

        if (event != null) {
            ticket.setEventName(event.getEventName());
            ticket.setEventId(event.getId());
            TicketResponseDto ticketResponseDto = ticketMapper.toDto(ticket);
            ticketResponseDto.setEvent(event);
            return ticketResponseDto;
        } else {
            throw new EventNotFoundException("Event not found");
        }
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
