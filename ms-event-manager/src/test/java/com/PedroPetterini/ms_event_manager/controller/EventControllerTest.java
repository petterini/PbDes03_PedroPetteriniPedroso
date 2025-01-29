package com.PedroPetterini.ms_event_manager.controller;

import com.PedroPetterini.ms_event_manager.dto.EventDto;
import com.PedroPetterini.ms_event_manager.exception.DuplicateEventException;
import com.PedroPetterini.ms_event_manager.exception.EventNotFoundException;
import com.PedroPetterini.ms_event_manager.exception.UnauthorizedException;
import com.PedroPetterini.ms_event_manager.model.Event;
import com.PedroPetterini.ms_event_manager.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @MockBean
    private EventService eventService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllEvents() throws Exception {
        Event event1 = new Event();
        Event event2 = new Event();
        List<Event> events = List.of(event1, event2);

        when(eventService.getAllEvents()).thenReturn(events);

        mockMvc.perform(get("/eventManagement/v1/get-all-events")).andExpect(status().isOk());
    }

    @Test
    void getAllEventsSorted() throws Exception {
        Event event1 = new Event();
        Event event2 = new Event();
        List<Event> events = List.of(event1, event2);

        when(eventService.getAllEventsSorted()).thenReturn(events);

        mockMvc.perform(get("/eventManagement/v1/get-all-events/sorted")).andExpect(status().isOk());
    }

    @Test
    void getEventByIdWithValidId() throws Exception {
        Event event = new Event("1", "Event1", LocalDateTime.now(), "97450000", "João Broll", "Centro", "Cacequi", "RS");

        when(eventService.getEventById(event.getId())).thenReturn(event);

        mockMvc.perform(get("/eventManagement/v1/1")).andExpect(status().isOk());
    }

    @Test
    void getEventByIdWithInvalidId() throws Exception {
        when(eventService.getEventById(anyString())).thenThrow(new EventNotFoundException("Event not found"));

        mockMvc.perform(get("/eventManagement/v1/1")).andExpect(status().isNotFound());
    }

    @Test
    void addEventWithValidData() throws Exception {
        EventDto eventDto = new EventDto("Event", LocalDateTime.now(), "97450-000");
        Event event = new Event("1", "Event1", LocalDateTime.now(), "97450000", "João Broll", "Centro", "Cacequi", "RS");


        when(eventService.addEvent(any(EventDto.class))).thenReturn(event);

        mockMvc.perform(post("/eventManagement/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().is(201));
    }

    @Test
    void addEventWithInvalidData() throws Exception {
        EventDto eventDto = new EventDto("", LocalDateTime.now(), "");

        mockMvc.perform(post("/eventManagement/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(result -> {
                    String responseContent = result.getResponse().getContentAsString();
                    System.out.println(responseContent);
                });
    }

    @Test
    void addEventWithInvalidDataButDuplicated() throws Exception {
        EventDto eventDto = new EventDto("Event", LocalDateTime.now(), "97450-000");

        doThrow(new DuplicateEventException("Event already exists")).when(eventService).addEvent(any(EventDto.class));

        mockMvc.perform(post("/eventManagement/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isConflict())
                .andExpect(result -> {
                    String responseContent = result.getResponse().getContentAsString();
                    System.out.println(responseContent);
                    assert responseContent.contains("Event already exists");
                });

        verify(eventService, times(1)).addEvent(any(EventDto.class));
    }

    @Test
    void updateEventWithValidIdAndDataAndNoDuplicates() throws Exception {
        Event event = new Event("1", "Event1", LocalDateTime.now(), "97450000", "João Broll", "Centro", "Cacequi", "RS");
        EventDto updateEventDto = new EventDto("Update", LocalDateTime.now(), "97450-000");
        Event updateEvent = new Event("1", "Update", LocalDateTime.now(), "97450000", "João Broll", "Centro", "Cacequi", "RS");

        when(eventService.updateEvent("1", updateEventDto)).thenReturn(updateEvent);

        mockMvc.perform(put("/eventManagement/v1/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateEventDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName").value("Update"));

    }

    @Test
    void updateEventWithInvalidId() throws Exception {
        EventDto eventDto = new EventDto("Update", LocalDateTime.now(), "97450-000");

        when(eventService.updateEvent(anyString(), any(EventDto.class))).thenThrow(new EventNotFoundException("Event not found"));

        mockMvc.perform(put("/eventManagement/v1/{id}", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Event not found"));
    }

    @Test
    void updateEventWithInvalidData() throws Exception {
        EventDto eventDto = new EventDto("", LocalDateTime.now(), "");

        mockMvc.perform(put("/eventManagement/v1/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updateEventWithInvalidDataButDuplicated() throws Exception {
        EventDto eventDto = new EventDto("Event", LocalDateTime.now(), "97450-000");

        when(eventService.updateEvent("1", eventDto)).thenThrow(new DuplicateEventException("Event already exists"));

        mockMvc.perform(put("/eventManagement/v1/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("Event already exists"));
    }

    @Test
    void deleteEventWithValidIdAndNoTickets() throws Exception {
        String eventId = "1";

        doNothing().when(eventService).deleteEvent(eventId);

        mockMvc.perform(delete("/eventManagement/v1/{id}", eventId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteEventWithInvalidId() throws Exception {
        String eventId = "1";

        doThrow(new EventNotFoundException("Event not found")).when(eventService).deleteEvent(eventId);

        mockMvc.perform(delete("/eventManagement/v1/{id}", eventId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Event not found"));
    }

    @Test
    void deleteEventWithValidIdAndHasTickets() throws Exception {
        String eventId = "1";

        doThrow(new UnauthorizedException("Unauthorized")).when(eventService).deleteEvent(eventId);

        mockMvc.perform(delete("/eventManagement/v1/{id}", eventId))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Unauthorized"));
    }
}