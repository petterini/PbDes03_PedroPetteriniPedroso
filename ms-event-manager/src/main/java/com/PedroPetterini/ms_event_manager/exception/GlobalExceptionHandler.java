package com.PedroPetterini.ms_event_manager.exception;

import com.PedroPetterini.ms_event_manager.dto.ErrorField;
import com.PedroPetterini.ms_event_manager.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponseDto handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        List<FieldError> fieldErrors = e.getFieldErrors();
        List<ErrorField> errorFields = fieldErrors.stream().map(fe -> new ErrorField(fe.getField(), fe.getDefaultMessage())).collect(Collectors.toList());
        return new ErrorResponseDto(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Validation error", errorFields);
    }
}
