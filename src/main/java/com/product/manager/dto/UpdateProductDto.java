package com.product.manager.dto;

public record UpdateProductDto(String productName, Double price, Long quantity, String description) {
}
