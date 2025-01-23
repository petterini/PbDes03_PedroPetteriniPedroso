package com.PedroPetterini.ms_event_manager.controller;

import com.PedroPetterini.ms_event_manager.model.Event;
import com.PedroPetterini.ms_event_manager.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(("eventManagement/v1"))
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

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

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable String id) {
        Event event = this.eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    @PostMapping
    public ResponseEntity<Event> addEvent(@RequestBody Event event) {
        var newEvent = this.eventService.addEvent(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(newEvent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable String id, @RequestBody Event event) {
        try {
            var e = this.eventService.updateEvent(id, event);
            return ResponseEntity.ok(e);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable String id) {
        this.eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

}
