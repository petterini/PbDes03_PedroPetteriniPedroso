package com.PedroPetterini.ms_ticket_manager.controller;

import com.PedroPetterini.ms_ticket_manager.dto.TicketResponseDto;
import com.PedroPetterini.ms_ticket_manager.exception.EventNotFoundException;
import com.PedroPetterini.ms_ticket_manager.exception.TicketNotFoundException;
import com.PedroPetterini.ms_ticket_manager.model.Event;
import com.PedroPetterini.ms_ticket_manager.model.Ticket;
import com.PedroPetterini.ms_ticket_manager.service.EventService;
import com.PedroPetterini.ms_ticket_manager.service.TicketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketController.class)
@ExtendWith(MockitoExtension.class)
class TicketControllerTest {

    @MockBean
    private TicketService ticketService;

    @MockBean
    private EventService eventService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTicketWithValidData() throws Exception {
        Event e1 = new Event();
        Ticket ticket = new Ticket("1", "João Junior", "41556135068", "joao.junior@email.com", "E1", "Concerto De Rock", 100.0, 20.0, true);
        TicketResponseDto ticketResponseDto = new TicketResponseDto("1", "João Junior", "41556135068", "joao.junior@email.com", e1, 100.0, 20.0);

        when(ticketService.createTicket(any(Ticket.class))).thenReturn(ticket);
        when(eventService.toDto(any(Ticket.class))).thenReturn(ticketResponseDto);

        mockMvc.perform(post("/ticketManagement/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticket)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ticketId").value("1"))
                .andExpect(jsonPath("$.customerName").value("João Junior"));
    }

    @Test
    void createTicketWithInvalidData() throws Exception {
        Ticket ticket = new Ticket();

        mockMvc.perform(post("/ticketManagement/v1")
        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ticket)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(result -> {
                    String responseContent = result.getResponse().getContentAsString();
                    System.out.println(responseContent);
                });

    }

    @Test
    void createTicketWithInvalidEventId() throws Exception {
        Ticket ticket = new Ticket("1", "João Junior", "41556135068", "joao.junior@email.com", "E1", "Concerto De Rock", 100.0, 20.0, true);

        doThrow(new EventNotFoundException("Event not found")).when(ticketService).createTicket(any(Ticket.class));

        mockMvc.perform(post("/ticketManagement/v1")
        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ticket)))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String responseContent = result.getResponse().getContentAsString();
                    System.out.println(responseContent);
                    assert responseContent.contains("Event not found");
                });
    }

    @Test
    void getAllTickets() throws Exception {
        TicketResponseDto ticket1 = new TicketResponseDto();
        TicketResponseDto ticket2 = new TicketResponseDto();
        List<TicketResponseDto> tickets = Arrays.asList(ticket1, ticket2);

        when(ticketService.getAllTickets()).thenReturn(tickets);

        mockMvc.perform(get("/ticketManagement/v1/get-all-tickets"))
                .andExpect(status().isOk());
    }

    @Test
    void getTicketByIdWithValidId() throws Exception {
        TicketResponseDto ticket1 = new TicketResponseDto();
        ticket1.setTicketId("1");

        when(ticketService.getTicketById(ticket1.getTicketId())).thenReturn(ticket1);

        mockMvc.perform(get("/ticketManagement/v1/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getTicketByIdWithInvalidId() throws Exception {
        when(ticketService.getTicketById(anyString())).thenThrow(new TicketNotFoundException("Ticket not found"));

        mockMvc.perform(get("/ticketManagement/v1/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTicketByIdWithValidId() throws Exception {
        String ticketId = "1";

        doNothing().when(ticketService).softDeleteTicketById(ticketId);

        mockMvc.perform(delete("/ticketManagement/v1/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTicketByIdWithInvalidId() throws Exception {
        doThrow(new TicketNotFoundException("Ticket not found")).when(ticketService).softDeleteTicketById("1");

        mockMvc.perform(delete("/ticketManagement/v1/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Ticket not found"));
    }

    @Test
    void checkTicketsByEventWithValidIdEvent() throws Exception {
        TicketResponseDto ticket1 = new TicketResponseDto();
        ticket1.setTicketId("1");

        TicketResponseDto ticket2 = new TicketResponseDto();
        ticket2.setTicketId("2");

        List<TicketResponseDto> tickets = Arrays.asList(ticket1, ticket2);

        when(ticketService.findByEventId("E1")).thenReturn(tickets);

        mockMvc.perform(get("/ticketManagement/v1/check-tickets-by-event/E1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ticketId").value("1"))
                .andExpect(jsonPath("$[1].ticketId").value("2"));
    }

    @Test
    void checkTicketsByEventWithInvalidIdEvent() throws Exception {
        when(ticketService.findByEventId("E1")).thenThrow(new TicketNotFoundException("Tickets not found for the event"));

        mockMvc.perform(get("/ticketManagement/v1/check-tickets-by-event/E1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Tickets not found for the event"));
    }


}