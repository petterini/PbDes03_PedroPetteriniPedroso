package com.PedroPetterini.ms_event_manager.mapper;

import com.PedroPetterini.ms_event_manager.dto.EventDto;
import com.PedroPetterini.ms_event_manager.dto.mapper.EventMapper;
import com.PedroPetterini.ms_event_manager.model.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EventMapperTest {

    private EventMapper eventMapper;
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        modelMapper = new ModelMapper();
        eventMapper = new EventMapper(modelMapper);
    }

    @Test
    void toEntity_shouldMapEventDtoToEvent() {
        EventDto eventDto = new EventDto("Event", LocalDateTime.now(), "97450-000");

        Event event = eventMapper.toEntity(eventDto);

        assertNotNull(event);
        assertEquals(eventDto.getEventName(), event.getEventName());
        assertEquals(eventDto.getDateTime(), event.getDateTime());
        assertEquals(eventDto.getCep(), event.getCep());
    }

    @Test
    void toDto_shouldMapEventToEventDto() {
        Event event = new Event();
        event.setEventName("Event");
        event.setDateTime(LocalDateTime.now());
        event.setCep("97450-000");

        EventDto eventDto = eventMapper.toDto(event);

        assertNotNull(eventDto);
        assertEquals(event.getEventName(), eventDto.getEventName());
        assertEquals(event.getDateTime(), eventDto.getDateTime());
        assertEquals(event.getCep(), eventDto.getCep());
    }

    @Test
    void toDtoList_shouldMapListOfEventToListOfEventDto() {
        Event event1 = new Event();
        event1.setEventName("Event");
        event1.setDateTime(LocalDateTime.now());
        event1.setCep("97450-000");

        Event event2 = new Event();
        event2.setEventName("Event 2");
        event2.setDateTime(LocalDateTime.now());
        event2.setCep("97450-000");

        List<Event> events = List.of(event1, event2);

        List<EventDto> eventDtos = eventMapper.toDto(events);

        assertNotNull(eventDtos);
        assertEquals(events.size(), eventDtos.size());

        assertEquals(events.get(0).getEventName(), eventDtos.get(0).getEventName());
        assertEquals(events.get(1).getEventName(), eventDtos.get(1).getEventName());
    }
}
