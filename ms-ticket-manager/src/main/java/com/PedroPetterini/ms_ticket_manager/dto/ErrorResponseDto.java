package com.PedroPetterini.ms_ticket_manager.dto;

import org.springframework.http.HttpStatus;

import java.util.List;

public record ErrorResponseDto(int status, String message, List<ErrorField> errors) {

    public static ErrorResponseDto defaultResponse(String message) {
        return new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), message, List.of());
    }

    public static ErrorResponseDto unauthorizedResponse(String message) {
        return new ErrorResponseDto(HttpStatus.UNAUTHORIZED.value(), message, List.of());
    }

    public static ErrorResponseDto ticketNotFoundResponse(String message) {
        return new ErrorResponseDto(HttpStatus.NOT_FOUND.value(), message, List.of());
    }

    public static ErrorResponseDto eventNotFoundResponse(String message) {
        return new ErrorResponseDto(HttpStatus.NOT_FOUND.value(), message, List.of());
    }
}
