package com.PedroPetterini.ms_ticket_manager.repository;

import com.PedroPetterini.ms_ticket_manager.model.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends MongoRepository<Ticket, String> {

    List<Ticket> findByEventId(String eventId);
}
