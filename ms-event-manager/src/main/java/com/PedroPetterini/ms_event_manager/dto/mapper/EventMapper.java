package com.PedroPetterini.ms_event_manager.dto.mapper;

import com.PedroPetterini.ms_event_manager.dto.EventDto;
import com.PedroPetterini.ms_event_manager.model.Event;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventMapper {

    private final ModelMapper modelMapper;

    public EventMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Event toEntity(EventDto eventDto) {
        Event event = modelMapper.map(eventDto, Event.class);
        return event;
    }

    public EventDto toDto(Event event) {
        EventDto eventDto = modelMapper.map(event, EventDto.class);
        return eventDto;
    }

    public List<EventDto> toDto(List<Event> events) {
        return events.stream().map(this::toDto).collect(Collectors.toList());
    }
}
