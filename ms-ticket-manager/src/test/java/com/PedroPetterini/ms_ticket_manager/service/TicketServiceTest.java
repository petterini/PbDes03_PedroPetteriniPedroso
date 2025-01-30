package com.PedroPetterini.ms_ticket_manager.service;

import com.PedroPetterini.ms_ticket_manager.consumer.EventConsumer;
import com.PedroPetterini.ms_ticket_manager.dto.TicketResponseDto;
import com.PedroPetterini.ms_ticket_manager.exception.EventNotFoundException;
import com.PedroPetterini.ms_ticket_manager.exception.TicketNotFoundException;
import com.PedroPetterini.ms_ticket_manager.model.Email;
import com.PedroPetterini.ms_ticket_manager.model.Event;
import com.PedroPetterini.ms_ticket_manager.model.Ticket;
import com.PedroPetterini.ms_ticket_manager.repository.TicketRepository;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private EventConsumer eventConsumer;

    @Mock
    private EventService eventService;

    @Mock
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @InjectMocks
    private TicketService ticketService;


    @Test
    void createTicketWithValidEventId() {
        Ticket ticket1 = new Ticket("1", "João Silva", "41556135068", "joao.silva@email.com", "E001", "Concerto de Rock", 100.0, 20.0, true);
        Event event1 = new Event("E001", "Concerto de Rock", LocalDateTime.now(), "97450-000", "João Broll", "Centro", "Cacequi", "RS");

        Response feignResponse = Response.builder()
                .status(HttpStatus.OK.value())
                .request(Request.create(Request.HttpMethod.GET, "", Map.of(), null, null, null))
                .build();

        when(eventConsumer.getEventResponse(ticket1.getEventId())).thenReturn(feignResponse);
        when(eventConsumer.getEvent(ticket1.getEventId())).thenReturn(event1);
        when(ticketRepository.save(ticket1)).thenReturn(ticket1);
        doNothing().when(emailService).sendEmail(any(Email.class));

        Ticket ticket = ticketService.createTicket(ticket1);

        assertThat(ticket).isEqualTo(ticket1);
        verify(eventConsumer).getEventResponse(ticket1.getEventId());

        InOrder inOrder = Mockito.inOrder(emailService, ticketRepository);
        inOrder.verify(emailService).sendEmail(any(Email.class));
        inOrder.verify(ticketRepository).save(ticket);

        ArgumentCaptor<Email> emailCaptor = ArgumentCaptor.forClass(Email.class);
        verify(emailService).sendEmail(emailCaptor.capture());
        Email sentEmail = emailCaptor.getValue();

        assertThat(sentEmail.getEmailTo()).isEqualTo("joao.silva@email.com");
        assertThat(sentEmail.getSubject()).isEqualTo("Ticket Confirmation from: Concerto de Rock");
        assertThat(sentEmail.getBody()).isEqualTo("Hello, João Silva, ticket Confirmation from: Concerto de Rock");
    }

    @Test
    void createTicketWithInvalidEventIdAndValidEventName() {
        Ticket ticket1 = new Ticket("1", "João Silva", "41556135068", "joao.silva@email.com", "E001", "Concerto de Rock", 100.0, 20.0, true);
        Event event1 = new Event("E001", "Concerto de Rock", LocalDateTime.now(), "97450-000", "João Broll", "Centro", "Cacequi", "RS");

        Response feignResponse1 = Response.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .request(Request.create(Request.HttpMethod.GET, "", Map.of(), null, null, null))
                .build();

        Response feignResponse2 = Response.builder()
                .status(HttpStatus.OK.value())
                .request(Request.create(Request.HttpMethod.GET, "", Map.of(), null, null, null))
                .build();

        when(eventConsumer.getEventResponse(ticket1.getEventId())).thenReturn(feignResponse1);
        when(eventConsumer.getEventResponseByName(ticket1.getEventName())).thenReturn(feignResponse2);
        when(eventConsumer.getEventByName(ticket1.getEventName())).thenReturn(event1);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket1);
        doNothing().when(emailService).sendEmail(any(Email.class));

        Ticket ticket = ticketService.createTicket(ticket1);

        assertThat(ticket).isEqualTo(ticket1);
        verify(eventConsumer).getEventResponse(ticket1.getEventId());
        verify(eventConsumer).getEventResponseByName(ticket1.getEventName());
        verify(eventConsumer).getEventByName(ticket1.getEventName());

        InOrder inOrder = Mockito.inOrder(emailService, ticketRepository);
        inOrder.verify(emailService).sendEmail(any(Email.class));
        inOrder.verify(ticketRepository).save(ticket);

        ArgumentCaptor<Email> emailCaptor = ArgumentCaptor.forClass(Email.class);
        verify(emailService).sendEmail(emailCaptor.capture());
        Email sentEmail = emailCaptor.getValue();

        assertThat(sentEmail.getEmailTo()).isEqualTo("joao.silva@email.com");
        assertThat(sentEmail.getSubject()).isEqualTo("Ticket Confirmation from: Concerto de Rock");
        assertThat(sentEmail.getBody()).isEqualTo("Hello, João Silva, ticket Confirmation from: Concerto de Rock");
    }


    @Test
    void createTicketWithInvalidEventIdAndInvalidEventName() {
        Ticket t1 = new Ticket();
        t1.setEventId("invalidEventId");
        t1.setEventName("invalidEventName");

        Response feignResponseId = Response.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .request(Request.create(Request.HttpMethod.GET, "", Map.of(), null, null, null))
                .build();

        Response feignResponseName = Response.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .request(Request.create(Request.HttpMethod.GET, "", Map.of(), null, null, null))
                .build();

        when(eventConsumer.getEventResponse(t1.getEventId())).thenReturn(feignResponseId);
        when(eventConsumer.getEventResponseByName(t1.getEventName())).thenReturn(feignResponseName);

        EventNotFoundException exception = assertThrows(EventNotFoundException.class,
                () -> ticketService.createTicket(t1));

        assertEquals("Error creating ticket, event not found", exception.getMessage());

        verify(eventConsumer).getEventResponse(t1.getEventId());
        verify(eventConsumer).getEventResponseByName(t1.getEventName());
        verify(ticketRepository, never()).save(any());
        verify(emailService, never()).sendEmail(any(Email.class));
    }


    @Test
    void getAllTickets() {
        Event e1 = new Event();
        Ticket ticket1 = new Ticket("1", "João Silva", "41556135068", "joao.silva@email.com", "E1", "Concerto De Rock", 100.0, 20.0, true);
        Ticket ticket2 = new Ticket("2", "Maria Oliveira", "43652783042", "maria.oliveira@email.com", "E1", "Concerto de Rock", 150.0, 30.0, true);
        List<Ticket> tickets = Arrays.asList(ticket1, ticket2);

        TicketResponseDto ticketDto1 = new TicketResponseDto("1", "João Silva", "41556135068", "joao.silva@email.com", e1, 100.0, 20.0);
        TicketResponseDto ticketDto2 = new TicketResponseDto("2", "Maria Oliveira", "43652783042", "maria.oliveira@email.com", e1, 150.0, 30.0);
        List<TicketResponseDto> ticketsDto = Arrays.asList(ticketDto1, ticketDto2);

        when(ticketRepository.findAll()).thenReturn(tickets);
        when(eventService.toDto(tickets)).thenReturn(ticketsDto);

        List<TicketResponseDto> result = ticketService.getAllTickets();

        assertThat(result).isNotEmpty();
        assertThat(result).isEqualTo(ticketsDto);
        assertThat(result).hasSize(2);
    }

    @Test
    void getTicketByIdWithValidId() {
        Ticket t1 = new Ticket();
        t1.setTicketId("1");
        TicketResponseDto t1Dto = new TicketResponseDto();
        t1Dto.setTicketId("1");

        when(ticketRepository.existsById(t1.getTicketId())).thenReturn(true);
        when(ticketRepository.findById(t1.getTicketId())).thenReturn(Optional.of(t1));
        when(eventService.toDto(t1)).thenReturn(t1Dto);

        TicketResponseDto result = ticketService.getTicketById(t1.getTicketId());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(t1Dto);
    }

    @Test
    void getTicketByIdWithInvalidId() {
        Ticket t1 = new Ticket();
        t1.setTicketId("1");

        when(ticketRepository.existsById(t1.getTicketId())).thenReturn(false);

        TicketNotFoundException exception = assertThrows(
                TicketNotFoundException.class,
                () -> ticketService.getTicketById(t1.getTicketId())
        );

        assertEquals("Ticket not found", exception.getMessage());

        verify(ticketRepository, never()).findById(any());
    }

    @Test
    void softDeleteTicketByIdWithValidId() {
        Ticket t1 = new Ticket();
        t1.setTicketId("1");
        t1.setActive(false);

        when(ticketRepository.existsById(t1.getTicketId())).thenReturn(true);
        when(ticketRepository.findById(t1.getTicketId())).thenReturn(Optional.of(t1));
        ArgumentCaptor<Ticket> ticketCaptor = ArgumentCaptor.forClass(Ticket.class);
        when(ticketRepository.save(ticketCaptor.capture())).thenReturn(t1);

        ticketService.softDeleteTicketById(t1.getTicketId());

        Ticket result = ticketCaptor.getValue();

        assertThat(result).isNotNull();
        assertThat(result.getTicketId()).isEqualTo(t1.getTicketId());
        assertThat(result.getActive()).isFalse();

    }

    @Test
    void softDeleteTicketByIdWithInvalidId() {
        String InvalidTicketId = "1";

        when(ticketRepository.existsById(InvalidTicketId)).thenReturn(false);

        assertThrows(TicketNotFoundException.class, () -> ticketService.softDeleteTicketById(InvalidTicketId));

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void findByEventIdWithValidEventId() {
        Event e1 = new Event();
        e1.setId("e1");

        Ticket t1 = new Ticket();
        Ticket t2 = new Ticket();
        List<Ticket> tickets = Arrays.asList(t1, t2);

        TicketResponseDto t1Dto = new TicketResponseDto();
        TicketResponseDto t2Dto = new TicketResponseDto();
        List<TicketResponseDto> ticketsDto = Arrays.asList(t1Dto, t2Dto);

        when(ticketRepository.existsById(t1.getTicketId())).thenReturn(true);
        when(ticketRepository.findByEventId(e1.getId())).thenReturn(tickets);
        when(eventService.toDto(tickets)).thenReturn(ticketsDto);

        var result = ticketService.findByEventId(e1.getId());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(ticketsDto);
        assertThat(result).hasSize(2);
    }

    @Test
    void findByEventIdWithInvalidEventId() {
        String invalidEventId = "1";

        when(ticketRepository.existsById(invalidEventId)).thenReturn(false);

        TicketNotFoundException exception = assertThrows(
                TicketNotFoundException.class,
                () -> ticketService.findByEventId(invalidEventId)
        );


        assertEquals("Ticket not found for this event", exception.getMessage());

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void updateTicketWithValidId() {
        Event e1 = new Event();
        Ticket ticket1 = new Ticket("1", "João Silva", "41556135068", "joao.silva@email.com", "E1", "Concerto De Rock", 100.0, 20.0, true);
        Ticket ticketToUpdate = new Ticket("1", "João Junior", "41556135068", "joao.junior@email.com", "E1", "Concerto De Rock", 100.0, 20.0, true);
        TicketResponseDto ticketResponseDto = new TicketResponseDto("1", "João Junior", "41556135068", "joao.junior@email.com", e1, 100.0, 20.0);

        when(ticketRepository.existsById(ticket1.getTicketId())).thenReturn(true);
        when(ticketRepository.findById(ticket1.getTicketId())).thenReturn(Optional.of(ticket1));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(eventService.toDto(ticketToUpdate)).thenReturn(ticketResponseDto);

        doNothing().when(emailService).sendEmail(any(Email.class));

        TicketResponseDto result = ticketService.updateTicket(ticket1.getTicketId(), ticketToUpdate);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(ticketResponseDto);

        verify(ticketRepository, times(1)).existsById(ticket1.getTicketId());
        verify(ticketRepository, times(1)).findById(ticket1.getTicketId());
        verify(ticketRepository, times(1)).save(ticket1);

        verify(emailService, times(1)).sendEmail(any(Email.class));
    }

    @Test
    void updateTicketWithInvalidId() {
        String ticketId = "invalidId";
        when(ticketRepository.existsById(ticketId)).thenReturn(false);

        TicketNotFoundException exception = assertThrows(TicketNotFoundException.class, () -> ticketService.updateTicket(ticketId, new Ticket()));

        assertEquals("Ticket not found", exception.getMessage());

        verify(ticketRepository, never()).findById(ticketId);
        verify(ticketRepository, never()).save(any(Ticket.class));
        verify(emailService, never()).sendEmail(any(Email.class));
    }
}