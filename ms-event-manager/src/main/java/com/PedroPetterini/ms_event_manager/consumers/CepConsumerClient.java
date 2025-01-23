package com.PedroPetterini.ms_event_manager.consumers;

import com.PedroPetterini.ms_event_manager.model.Event;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "cep-consumer",
        url="https://viacep.com.br/")
public interface CepConsumerClient {

    @GetMapping(value = "/ws/{cep}/json")
    Event getCepInfo(@PathVariable("cep") String cep);

}
