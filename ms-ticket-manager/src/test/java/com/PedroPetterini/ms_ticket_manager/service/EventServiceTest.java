package com.PedroPetterini.ms_ticket_manager.service;

import com.PedroPetterini.ms_ticket_manager.consumer.EventConsumer;
import com.PedroPetterini.ms_ticket_manager.dto.TicketResponseDto;
import com.PedroPetterini.ms_ticket_manager.dto.mapper.TicketMapper;
import com.PedroPetterini.ms_ticket_manager.exception.EventNotFoundException;
import com.PedroPetterini.ms_ticket_manager.model.Event;
import com.PedroPetterini.ms_ticket_manager.model.Ticket;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private TicketMapper ticketMapper;

    @Mock
    private EventConsumer eventConsumer;

    @InjectMocks
    private EventService eventService;

    @Test
    void toDtoWithValidEventId() {
        Ticket ticket = new Ticket("1", "João Silva", "41556135068", "joao.silva@email.com", "E001", "Concerto de Rock", 100.0, 20.0, true);
        Event event = new Event("E001", "Concerto de Rock", LocalDateTime.now(), "97450-000", "João Broll","Centro", "Cacequi", "RS");
        TicketResponseDto ticketResponseDtoMock = new TicketResponseDto("1", "João Silva", "41556135068", "joao.silva@email.com", event, 100.0, 20.0);

        when(eventConsumer.getEventResponse(ticket.getEventId())).thenReturn(mockResponse(HttpStatus.OK.value()));
        when(eventConsumer.getEvent(ticket.getEventId())).thenReturn(event);
        when(ticketMapper.toDto(ticket)).thenReturn(ticketResponseDtoMock);

        TicketResponseDto result = eventService.toDto(ticket);

        assertNotNull(result);
        assertEquals(event, result.getEvent());
        assertEquals(ticket.getEventId(), result.getEvent().getId());
        verify(eventConsumer, times(1)).getEventResponse(ticket.getEventId());
        verify(eventConsumer, times(1)).getEvent(ticket.getEventId());
        verify(ticketMapper, times(1)).toDto(ticket);
    }

    @Test
    void toDtoWithInvalidEventId() {
        Ticket ticket = new Ticket("1", "João Silva", "41556135068", "joao.silva@email.com", "E001", "Concerto de Rock", 100.0, 20.0, true);

        when(eventConsumer.getEventResponse(ticket.getEventId())).thenReturn(mockResponse(HttpStatus.NOT_FOUND.value()));

        assertThrows(EventNotFoundException.class, () -> eventService.toDto(ticket));
        verify(eventConsumer, times(1)).getEventResponse(ticket.getEventId());
        verify(eventConsumer, never()).getEvent(anyString());
        verify(ticketMapper, never()).toDto(any(Ticket.class));
    }

    @Test
    void toDtoListWithValidEventId() {
        Ticket ticket1 = new Ticket("1", "João Silva", "41556135068", "joao.silva@email.com", "E001", "Concerto de Rock", 100.0, 20.0, true);
        Ticket ticket2 = new Ticket("2", "Maria Oliveira", "43652783042", "maria.oliveira@email.com", "E002", "Teatro Musical", 150.0, 30.0, true);

        Event event1 = new Event("E001", "Concerto de Rock", LocalDateTime.now(), "97450-000", "João Broll","Centro", "Cacequi", "RS");
        Event event2 = new Event("E002", "Concerto de Rock 2", LocalDateTime.now(), "97450-000", "João Broll","Centro", "Cacequi", "RS");

        TicketResponseDto responseDto1 = new TicketResponseDto("1", "João Silva", "41556135068", "joao.silva@email.com", event1, 100.0, 20.0);
        TicketResponseDto responseDto2 = new TicketResponseDto("2", "João Silva", "41556135068", "joao.silva@email.com", event2, 100.0, 20.0);

        List<Ticket> tickets = List.of(ticket1, ticket2);

        when(eventConsumer.getEvent(ticket1.getEventId())).thenReturn(event1);
        when(eventConsumer.getEvent(ticket2.getEventId())).thenReturn(event2);
        when(ticketMapper.toDto(ticket1)).thenReturn(responseDto1);
        when(ticketMapper.toDto(ticket2)).thenReturn(responseDto2);

        List<TicketResponseDto> result = eventService.toDto(tickets);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(event1, result.get(0).getEvent());
        assertEquals(event2, result.get(1).getEvent());

        verify(eventConsumer, times(1)).getEvent(ticket1.getEventId());
        verify(eventConsumer, times(1)).getEvent(ticket2.getEventId());
        verify(ticketMapper, times(1)).toDto(ticket1);
        verify(ticketMapper, times(1)).toDto(ticket2);
    }

    @Test
    void toDtoWithEmptyListOfTickets() {
        List<TicketResponseDto> result = eventService.toDto(new ArrayList<>());

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(eventConsumer, never()).getEvent(anyString());
        verify(ticketMapper, never()).toDto(any(Ticket.class));
    }

    private feign.Response mockResponse(int status) {
        return feign.Response.builder()
                .status(status)
                .request(feign.Request.create(feign.Request.HttpMethod.GET, "", Map.of(), null, null, null))
                .build();
    }
}