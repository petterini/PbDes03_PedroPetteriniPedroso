package com.PedroPetterini.ms_ticket_manager.consumer;

import com.PedroPetterini.ms_ticket_manager.model.Event;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "event-consumer",
        url="http://localhost:8080/eventManagement/v1/")
public interface EventConsumer {

    @GetMapping(value = "/{id}")
    Response getEventResponse(@PathVariable String id);

    @GetMapping(value = "/get-by-name/{eventName}")
    Response getEventResponseByName(@PathVariable String eventName);

    @GetMapping(value = "/{id}")
    Event getEvent(@PathVariable String id);

    @GetMapping(value = "/get-by-name/{eventName}")
    Event getEventByName(@PathVariable String eventName);


}
