package com.product.manager.dto;

import com.product.manager.exceptions.RoleValidationException;
import com.product.manager.exceptions.UserValidationException;
import org.springframework.http.HttpStatus;

import java.util.Set;

public record RegisterRequest(String username, String password, Set<String> roles) {
    public RegisterRequest {
        if (username == null || username.isBlank()) {
            throw new UserValidationException("Username cannot be null or empty", HttpStatus.BAD_REQUEST);
        }
        if (password == null || password.isBlank()) {
            throw new UserValidationException("password cannot be null or empty", HttpStatus.BAD_REQUEST);
        }
        if (roles == null || roles.isEmpty()) {
            throw new RoleValidationException("Roles cannot be null or empty", HttpStatus.BAD_REQUEST);
        }
    }
}
