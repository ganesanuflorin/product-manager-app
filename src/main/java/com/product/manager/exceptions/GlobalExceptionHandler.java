package com.product.manager.exceptions;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductValidationException.class)
    public ResponseEntity<String> handleProductValidationException(ProductValidationException ex) {
        log.warn("Product validation failed: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
