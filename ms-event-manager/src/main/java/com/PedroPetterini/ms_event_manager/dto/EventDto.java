package com.PedroPetterini.ms_event_manager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventDto {
    @NotBlank(message = "The event name is required.")
    private String eventName;

    @NotNull(message = "The event date and time are required.")
    private LocalDateTime dateTime;

    @NotBlank(message = "The CEP code is required.")
    @Pattern(regexp = "\\d{5}-\\d{3}", message = "The CEP code must follow the format 99999-999.")
    private String cep;
}
