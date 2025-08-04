package com.product.manager.dto;

import com.product.manager.exceptions.UserValidationException;
import org.springframework.http.HttpStatus;

public record LoginRequest(String username, String password) {

    public LoginRequest {
        if (username == null || username.isBlank()) {
            throw new UserValidationException("Username cannot be null or empty", HttpStatus.BAD_REQUEST);
        }
        if (password == null || password.isBlank()) {
            throw new UserValidationException("password cannot be null or empty", HttpStatus.BAD_REQUEST);
        }
    }
}
