package com.PedroPetterini.ms_ticket_manager.dto.mapper;

import com.PedroPetterini.ms_ticket_manager.dto.TicketResponseDto;
import com.PedroPetterini.ms_ticket_manager.model.Ticket;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TicketMapper {

    private final ModelMapper modelMapper;

    public TicketMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Ticket toEntity(TicketResponseDto ticketResponseDto) {
        Ticket ticket = modelMapper.map(ticketResponseDto, Ticket.class);
        return ticket;
    }

    public TicketResponseDto toDto(Ticket ticket) {

        TicketResponseDto dto = modelMapper.map(ticket, TicketResponseDto.class);
        return dto;
    }

    public List<TicketResponseDto> toDto(List<Ticket> tickets) {
        return tickets.stream().map(this::toDto).collect(Collectors.toList());
    }
}
