package com.product.manager.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RoleValidationException extends RuntimeException{

    private final HttpStatus status;

    public RoleValidationException(String message, HttpStatus statusCode) {
        super(message);
        this.status = statusCode;
    }
}
