package com.PedroPetterini.ms_event_manager.service;

import com.PedroPetterini.ms_event_manager.model.Event;
import com.PedroPetterini.ms_event_manager.repository.EventRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getAllEventsSorted() {
        return eventRepository.findAll(Sort.by(Sort.Direction.ASC, "eventName"));
    }

    public Event getEventById(String id) {
        return eventRepository.findById(id).orElseThrow(()-> new RuntimeException("User not found"));
    }

    public Event addEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event updateEvent(String id, Event event) {
            Event e = eventRepository.findById(id).orElseThrow(()-> new RuntimeException("User not found"));
            updateData(e, event);
            return eventRepository.save(event);
    }

    public void updateData(Event e, Event event) {
        BeanUtils.copyProperties(event, e, "id");
    }

    public void deleteEvent(String id) {
        eventRepository.deleteById(id);
    }
}
