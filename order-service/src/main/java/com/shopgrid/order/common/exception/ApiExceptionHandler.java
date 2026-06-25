package com.shopgrid.order.common.exception;

import com.shopgrid.order.common.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        return new ErrorResponse(404, e.getMessage(), Instant.now());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ErrorResponse handleAccessDeniedException(AccessDeniedException e) {
        return new ErrorResponse(403, e.getMessage(), Instant.now());
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleServiceUnavailableException(ServiceUnavailableException e) {
        return new ErrorResponse(503, e.getMessage(), Instant.now());
    }

    @ExceptionHandler(OrderAlreadyException.class)
    public ErrorResponse handleOrderAlreadyException(OrderAlreadyException e) {
        return new ErrorResponse(500, e.getMessage(), Instant.now());
    }

    @ExceptionHandler(InvalidOrderStatusTransitionException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleInvalidOrderStatusTransitionException(InvalidOrderStatusTransitionException e) {
        return new ErrorResponse(500, e.getMessage(), Instant.now());
    }

    @ExceptionHandler(CannotCancelAllItemsException.class)
    public ErrorResponse handleCannotCancelAllItemsException(CannotCancelAllItemsException e) {
        return new ErrorResponse(500, e.getMessage(), Instant.now());
    }

    @ExceptionHandler(EventSerializationException.class)
    public ErrorResponse handleEventSerializationException(EventSerializationException e) {
        return new ErrorResponse(500, e.getMessage(), Instant.now());
    }
}
