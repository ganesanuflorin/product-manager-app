package com.product.manager.service;

import com.product.manager.dto.ProductDto;
import com.product.manager.exceptions.ProductValidationException;
import com.product.manager.mapper.ProductMapper;
import com.product.manager.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public String addProduct(ProductDto request) {

        if (productRepository.existsByCode(request.code())) {
            log.warn("Attempted to add product with duplicate code: {}", request.code());
            throw new ProductValidationException("Product with code " + request.code() + " already exists.", HttpStatus.CONFLICT);
        }
        var product = productMapper.toEntity(request);
        productRepository.save(product);
        log.info("Product '{}' added with code '{}'", product.getProductName(), product.getCode());

        return "Product " + product.getProductName() + " added successfully.";
    }

    public ProductDto getProductByCode(Long code) {
        return productRepository.findByCode(code)
                .map(productMapper::toDto)
                .orElseThrow(() -> new ProductValidationException("Product with code " + code + " not found.", HttpStatus.NOT_FOUND));

    }

    @Transactional
    public String updateProduct(ProductDto request) {

        if (!productRepository.existsByCode(request.code())) {
            log.warn("Attempted to update non-existing product with code: {}", request.code());
            throw new ProductValidationException("Product with code " + request.code() + " does not exist.", HttpStatus.NOT_FOUND);
        }

        var product = productMapper.toEntity(request);
        log.info("Product '{}' updated with code '{}'", product.getProductName(), product.getCode());

        return "Product " + product.getProductName() + " updated successfully.";
    }
}
