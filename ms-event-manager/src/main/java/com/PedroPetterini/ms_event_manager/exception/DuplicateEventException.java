package com.PedroPetterini.ms_event_manager.exception;

public class DuplicateEventException extends RuntimeException {
    public DuplicateEventException(String message) {
        super(message);
    }
}
