package com.product.manager.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserValidationException extends RuntimeException {

    private final HttpStatus status;

    public UserValidationException(String message, HttpStatus statusCode) {
        super(message);
        this.status = statusCode;
    }
}
