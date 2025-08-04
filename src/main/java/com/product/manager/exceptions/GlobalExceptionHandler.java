package com.product.manager.exceptions;

import com.product.manager.dto.GenericResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductValidationException.class)
    public ResponseEntity<GenericResponse<?>> handleProductValidationException(ProductValidationException ex) {
        return buildErrorResponse(ex, ex.getStatus());
    }

    @ExceptionHandler(UserValidationException.class)
    public ResponseEntity<GenericResponse<?>> handleUserValidationException(UserValidationException ex) {
        return buildErrorResponse(ex, ex.getStatus());
    }

    @ExceptionHandler(RoleValidationException.class)
    public ResponseEntity<GenericResponse<?>> handleRoleValidationException(RoleValidationException ex) {
        return buildErrorResponse(ex, ex.getStatus());
    }

    private ResponseEntity<GenericResponse<?>> buildErrorResponse(RuntimeException ex, HttpStatus status) {
        GenericResponse<?> errorResponse = new GenericResponse<>(
                status.value(),
                false,
                ex.getMessage(),
                null
        );

        log.error("Failed: {}", ex.getMessage());
        return ResponseEntity.status(status).body(errorResponse);
    }
}
