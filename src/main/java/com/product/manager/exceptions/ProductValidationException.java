package com.product.manager.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ProductValidationException extends RuntimeException {

    private final HttpStatus status;

    public ProductValidationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
