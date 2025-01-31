package com.PedroPetterini.ms_event_manager.controller;

import com.PedroPetterini.ms_event_manager.dto.ErrorResponseDto;
import com.PedroPetterini.ms_event_manager.dto.EventDto;
import com.PedroPetterini.ms_event_manager.exception.DuplicateEventException;
import com.PedroPetterini.ms_event_manager.exception.EventNotFoundException;
import com.PedroPetterini.ms_event_manager.exception.UnauthorizedException;
import com.PedroPetterini.ms_event_manager.model.Event;
import com.PedroPetterini.ms_event_manager.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(("eventManagement/v1"))
public class EventController {

    private final EventService eventService;

    @GetMapping("/get-all-events")
    public ResponseEntity<List<Event>> getAllEvents() {
        var events = this.eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/get-all-events/sorted")
    public ResponseEntity<List<Event>> getAllEventsSorted() {
        var events = this.eventService.getAllEventsSorted();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/get-event/{id}")
    public ResponseEntity<Object> getEventById(@PathVariable String id) {
        try {
            Event event = this.eventService.getEventById(id);
            return ResponseEntity.ok(event);
        } catch (EventNotFoundException e) {
            var errorMessage = ErrorResponseDto.eventNotFoundResponse(e.getMessage());
            return ResponseEntity.status(errorMessage.status()).body(errorMessage);
        }
    }

    @GetMapping("/get-by-name/{eventName}")
    public ResponseEntity<Object> getEventByEventName(@PathVariable String eventName) {
        try{
            Event event = this.eventService.getEventByName(eventName);
            return ResponseEntity.ok(event);
        }catch (EventNotFoundException e){
            var errorMessage = ErrorResponseDto.eventNotFoundResponse(e.getMessage());
            return ResponseEntity.status(errorMessage.status()).body(errorMessage);
        }
    }

    @PostMapping("/create-event")
    public ResponseEntity<Object> addEvent(@Valid @RequestBody EventDto eventDto) {
        try {
            var newEvent = this.eventService.addEvent(eventDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(newEvent);
        } catch (DuplicateEventException e) {
            var errorMessage = ErrorResponseDto.conflictResponse(e.getMessage());
            return ResponseEntity.status(errorMessage.status()).body(errorMessage);
        }
    }

    @PutMapping("/update-event/{id}")
    public ResponseEntity<Object> updateEvent(@PathVariable String id, @Valid @RequestBody EventDto eventDto) {
        try {
            var e = this.eventService.updateEvent(id, eventDto);
            return ResponseEntity.ok(e);
        } catch (DuplicateEventException e) {
            var errorMessage = ErrorResponseDto.conflictResponse(e.getMessage());
            return ResponseEntity.status(errorMessage.status()).body(errorMessage);
        } catch (EventNotFoundException e) {
            var errorMessage = ErrorResponseDto.eventNotFoundResponse(e.getMessage());
            return ResponseEntity.status(errorMessage.status()).body(errorMessage);
        }
    }

    @DeleteMapping("/delete-event/{id}")
    public ResponseEntity<Object> deleteEvent(@PathVariable String id) {
        try {
            this.eventService.deleteEvent(id);
            return ResponseEntity.noContent().build();
        } catch (UnauthorizedException e) {
            var errorMessage = ErrorResponseDto.unauthorizedResponse(e.getMessage());
            return ResponseEntity.status(errorMessage.status()).body(errorMessage);
        } catch (EventNotFoundException e) {
            var errorMessage = ErrorResponseDto.eventNotFoundResponse(e.getMessage());
            return ResponseEntity.status(errorMessage.status()).body(errorMessage);
        }
    }

}
