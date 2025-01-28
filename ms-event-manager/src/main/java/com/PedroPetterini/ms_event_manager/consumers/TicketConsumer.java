package com.PedroPetterini.ms_event_manager.consumers;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "ticket-consumer",
        url="http://localhost:8081/ticketManagement/v1/")
public interface TicketConsumer {
    @GetMapping("/check-tickets-by-event/{eventId}")
    Response getEventResponse(@PathVariable String eventId);
}
