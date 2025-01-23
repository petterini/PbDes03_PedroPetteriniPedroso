package com.PedroPetterini.ms_event_manager.service;

import com.PedroPetterini.ms_event_manager.consumers.CepConsumerClient;
import com.PedroPetterini.ms_event_manager.model.Event;
import com.PedroPetterini.ms_event_manager.repository.EventRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final CepConsumerClient cepConsumerClient;


    public EventService(EventRepository eventRepository, CepConsumerClient cepConsumerClient) {
        this.eventRepository = eventRepository;
        this.cepConsumerClient = cepConsumerClient;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getAllEventsSorted() {
        return eventRepository.findAll(Sort.by(Sort.Direction.ASC, "eventName"));
    }

    public Event getEventById(String id) {
        return eventRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Event addEvent(Event event) {
        var newEvent = addCepData(event);
        newEvent.setEventName(event.getEventName());
        newEvent.setDateTime(event.getDateTime());
        return eventRepository.save(newEvent);
    }

    private Event addCepData(Event event) {
        return cepConsumerClient.getCepInfo(event.getCep().replace("-", ""));
    }

    public Event updateEvent(String id, Event event) {
        Event e = eventRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        var newEvent = addCepData(event);
        newEvent.setEventName(event.getEventName());
        newEvent.setDateTime(event.getDateTime());
        updateData(e, newEvent);
        return eventRepository.save(newEvent);
    }

    public void updateData(Event e, Event event) {
        BeanUtils.copyProperties(event, e, "id");
    }

    public void deleteEvent(String id) {
        eventRepository.deleteById(id);
    }


}
