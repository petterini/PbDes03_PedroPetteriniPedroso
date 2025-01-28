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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final CepConsumerClient cepConsumerClient;
    private final EventMapper eventMapper;
    private final EventValidator eventValidator;
    private final TicketConsumer ticketConsumer;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getAllEventsSorted() {
        return eventRepository.findAll(Sort.by(Sort.Direction.ASC, "eventName"));
    }

    public Event getEventById(String id) {
        if (eventRepository.existsById(id)) {
            return eventRepository.findById(id).orElse(null);
        }
        throw new EventNotFoundException("Inexistent event");
    }

    public Event addEvent(EventDto eventDto) {
        var event = eventMapper.toEntity(eventDto);
        eventValidator.validate(event);
        var newEvent = addCepData(event);
        return eventRepository.save(newEvent);
    }

    private Event addCepData(Event event) {
        var newEvent = cepConsumerClient.getCepInfo(event.getCep().replace("-", ""));
        newEvent.setEventName(event.getEventName());
        newEvent.setDateTime(event.getDateTime());
        if (event.getId() != null) {
            newEvent.setId(event.getId());
        }
        return newEvent;
    }

    public Event updateEvent(String id, EventDto eventDto) {
        if (eventRepository.existsById(id)) {
            Event e = eventRepository.findById(id).orElse(null);
            var event = eventMapper.toEntity(eventDto);
            event.setId(e.getId());
            e = addCepData(event);
            e.setEventName(event.getEventName());
            e.setDateTime(event.getDateTime());
            eventValidator.validate(e);
            return eventRepository.save(e);
        } else {
            throw new EventNotFoundException("Inexistent event");
        }
    }


    public void deleteEvent(String id) {
        if (eventRepository.existsById(id)) {
            if (ticketConsumer.getEventResponse(id).status() == HttpStatus.NO_CONTENT.value())
                eventRepository.deleteById(id);
            else {
                System.out.println(ticketConsumer.getEventResponse(id).status());
                System.out.println(HttpStatus.NO_CONTENT.value());
                throw new UnauthorizedException("Impossible to delete an event that already has tickets.");
            }
        }else{
            throw new EventNotFoundException("Inexistent event");
        }
    }


}

