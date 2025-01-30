package com.PedroPetterini.ms_event_manager.repository;

import com.PedroPetterini.ms_event_manager.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {

    Optional<Event> findByEventNameAndDateTimeAndCep(String eventName, LocalDateTime dateTime, String cep);

    Optional<Event> findByEventName(String eventName);
}
