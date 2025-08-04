package com.product.manager.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record GenericResponse<T>(
        @Min(100) @Max(599)
        int status,

        boolean success,

        @NotBlank
        String message,

        T data
) {
}
