package com.PedroPetterini.ms_event_manager.repository;

import com.PedroPetterini.ms_event_manager.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {
}
