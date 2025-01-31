package com.PedroPetterini.ms_event_manager.validator;

import com.PedroPetterini.ms_event_manager.exception.DuplicateEventException;
import com.PedroPetterini.ms_event_manager.model.Event;
import com.PedroPetterini.ms_event_manager.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EventValidator {

    private final EventRepository eventRepository;


    public void validate(Event event) {
        if(eventExists(event)) {
            throw new DuplicateEventException("Event already exists");
        }
    }

    private Boolean eventExists(Event event) {
        Optional<Event> optionalEvent = eventRepository
                .findByEventNameAndDateTimeAndCep(event.getEventName(), event.getDateTime(), event.getCep());

        if(event.getId() == null) {
            return optionalEvent.isPresent();
        }

        if(optionalEvent.isPresent()){
            return !(event.getId().equals(optionalEvent.get().getId()));
        }

        return false;
    }
}
