package com.PedroPetterini.ms_event_manager.service;

import com.PedroPetterini.ms_event_manager.consumers.CepConsumerClient;
import com.PedroPetterini.ms_event_manager.consumers.TicketConsumer;
import com.PedroPetterini.ms_event_manager.dto.EventDto;
import com.PedroPetterini.ms_event_manager.dto.mapper.EventMapper;
import com.PedroPetterini.ms_event_manager.exception.EventNotFoundException;
import com.PedroPetterini.ms_event_manager.exception.UnauthorizedException;
import com.PedroPetterini.ms_event_manager.model.Event;
import com.PedroPetterini.ms_event_manager.repository.EventRepository;
import com.PedroPetterini.ms_event_manager.validator.EventValidator;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CepConsumerClient cepConsumerClient;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private EventValidator eventValidator;

    @Mock
    private TicketConsumer ticketConsumer;

    @InjectMocks
    private EventService eventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    void getAllEvents() {
        Event event = new Event("1", "Event1", LocalDateTime.now(), "97450000", "João Broll", "Centro", "Cacequi", "RS");
        Event event1 = new Event("2", "Event2", LocalDateTime.now(), "97450000", "João Broll", "Centro", "Cacequi", "RS");
        List<Event> events = Arrays.asList(event, event1);

        when(eventRepository.findAll()).thenReturn(events);

        List<Event> result = eventService.getAllEvents();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(event));
        assertTrue(result.contains(event1));
    }

    @Test
    void getAllEventsSorted() {
        Event event = new Event("1", "Event1", LocalDateTime.now(), "97450000", "João Broll", "Centro", "Cacequi", "RS");
        Event event1 = new Event("2", "Event2", LocalDateTime.now(), "97450000", "João Broll", "Centro", "Cacequi", "RS");
        List<Event> events = Arrays.asList(event, event1);

        when(eventRepository.findAll(Sort.by(Sort.Direction.ASC, "eventName"))).thenReturn(events);

        List<Event> result = eventService.getAllEventsSorted();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(result.get(0), event);
        assertEquals(result.get(1), event1);
        assertTrue(result.contains(event1));
    }

    @Test
    void getEventById_Found() {
        Event event = new Event("1", "Event1", LocalDateTime.now(), "97450000", "João Broll", "Centro", "Cacequi", "RS");

        when(eventRepository.findById("1")).thenReturn(Optional.of(event));

        Event result = eventService.getEventById("1");

        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Event1", result.getEventName());
    }

    @Test
    void getEventById_NotFound() {
        when(eventRepository.findById("1")).thenReturn(Optional.empty());

        EventNotFoundException exception = assertThrows(EventNotFoundException.class, () -> eventService.getEventById("1"));

        assertEquals("Inexistent event", exception.getMessage());
    }

    @Test
    void addEvent() {
        Event event = new Event("1", "Event1", LocalDateTime.now(), "97450000", "João Broll", "Centro", "Cacequi", "RS");
        Event event1 = new Event();
        event1.setEventName("Event1");
        event1.setDateTime(event.getDateTime());
        event1.setCep("97450000");
        EventDto eventDto = new EventDto("Event1", LocalDateTime.now(), "97450000");

        when(eventMapper.toEntity(eventDto)).thenReturn(event1);
        doNothing().when(eventValidator).validate(event1);
        when(eventRepository.save(event)).thenReturn(event);
        when(cepConsumerClient.getCepInfo("97450000")).thenReturn(event);


        Event result = eventService.addEvent(eventDto);

        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Event1", result.getEventName());
        assertEquals("97450000", result.getCep());
    }

    @Test
    void updateEventWithValidEvent() {
        EventDto eventDto = new EventDto("Event1", LocalDateTime.now(), "97450000");
        Event event = new Event("1", "Event1", LocalDateTime.now(), "97450000", "João Broll", "Centro", "Cacequi", "RS");
        Event eventUpdate = new Event("1", "EventUpdate", LocalDateTime.now(), "97450000", "João Broll", "Centro", "Cacequi", "RS");

        when(eventRepository.existsById(event.getId())).thenReturn(true);
        when(eventRepository.findById("1")).thenReturn(Optional.of(event));
        when(eventMapper.toEntity(eventDto)).thenReturn(eventUpdate);
        doNothing().when(eventValidator).validate(eventUpdate);
        when(cepConsumerClient.getCepInfo("97450000")).thenReturn(eventUpdate);
        when(eventRepository.save(eventUpdate)).thenReturn(eventUpdate);

        Event result = eventService.updateEvent("1", eventDto);

        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("EventUpdate", result.getEventName());
        assertEquals("97450000", result.getCep());
    }

    @Test
    void updateEventWithInvalidEvent() {
        EventDto eventDto = new EventDto("Event1", LocalDateTime.now(), "97450000");

        when(eventRepository.existsById("1")).thenReturn(false);

        assertThrows(EventNotFoundException.class, () -> eventService.updateEvent("1", eventDto));
    }

    @Test
    void deleteEventWithValidEventAndNoTickets() {
        String eventId = "1";

        Response feignResponse = Response.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .request(Request.create(Request.HttpMethod.GET, "", Map.of(), null, null, null))
                .build();

        when(eventRepository.existsById(eventId)).thenReturn(true);
        when(ticketConsumer.getEventResponse(eventId)).thenReturn(feignResponse);

        eventService.deleteEvent(eventId);

        verify(eventRepository, times(1)).deleteById(eventId);
        verify(ticketConsumer, times(1)).getEventResponse(eventId);

    }

    @Test
    void deleteEventWithInvalidEventAndNoTickets() {
        String eventId = "1";

        when(eventRepository.existsById(eventId)).thenReturn(false);

        assertThrows(EventNotFoundException.class, () -> eventService.deleteEvent(eventId));

    }

    @Test
    void deleteEventWithValidEventAndTickets() {
        String eventId = "1";

        Response feignResponse = Response.builder()
                .status(HttpStatus.OK.value())
                .request(Request.create(Request.HttpMethod.GET, "", Map.of(), null, null, null))
                .build();

        when(eventRepository.existsById(eventId)).thenReturn(true);
        when(ticketConsumer.getEventResponse(eventId)).thenReturn(feignResponse);

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> eventService.deleteEvent(eventId));
        assertEquals("Impossible to delete an event that already has tickets.", exception.getMessage());

        verify(eventRepository, never()).deleteById(anyString());
        verify(ticketConsumer, times(1)).getEventResponse(eventId);

    }
}