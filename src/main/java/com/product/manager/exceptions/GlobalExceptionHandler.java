package com.product.manager.exceptions;

import com.product.manager.dto.GenericResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductValidationException.class)
    public ResponseEntity<GenericResponse<?>> handleProductValidationException(ProductValidationException ex) {
        GenericResponse<?> errorResponse = new GenericResponse<>(
                ex.getStatus().value(),
                false,
                ex.getMessage(),
                null
        );

        log.error("Failed: {}", ex.getMessage());
        return ResponseEntity.status(ex.getStatus())
                .body(errorResponse);
    }
}
