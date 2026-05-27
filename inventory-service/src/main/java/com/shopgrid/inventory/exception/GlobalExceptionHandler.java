package com.shopgrid.inventory.exception;

import com.shopgrid.inventory.domain.dto.response.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientStockException.class)
    public ErrorResponse handleInsufficientStockException(InsufficientStockException e) {
        return new ErrorResponse(403, e.getMessage() , Instant.now());
    }

    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        return new ErrorResponse(404, e.getMessage() , Instant.now());
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ErrorResponse handleAlreadyExistsException(AlreadyExistsException e) {
        return new ErrorResponse(409, e.getMessage() , Instant.now());
    }
}


