package com.natanxds.stock.controller;

import com.natanxds.stock.exception.InsufficientStockException;
import com.natanxds.stock.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleOrderAlreadyExistsException(ResourceNotFoundException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<String> handleOrderAlreadyExistsException(InsufficientStockException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
