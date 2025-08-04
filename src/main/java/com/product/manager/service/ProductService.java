package com.product.manager.service;

import com.product.manager.dto.GenericResponse;
import com.product.manager.dto.ProductDto;
import com.product.manager.dto.UpdateProductDto;
import com.product.manager.exceptions.ProductValidationException;
import com.product.manager.mapper.ProductMapper;
import com.product.manager.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public GenericResponse<Void> addProduct(ProductDto productDto) {

        if (productRepository.existsByCode(productDto.code())) {
            throw new ProductValidationException("Product with code " + productDto.code() + " already exists.", HttpStatus.CONFLICT);
        }
        var product = productMapper.toEntity(productDto);
        productRepository.save(product);
        log.info("Product '{}' added with code '{}'", product.getProductName(), product.getCode());

        return new GenericResponse<>(HttpStatus.CREATED.value(), true,"Product added successfully", null);
    }

    public GenericResponse<ProductDto> getProductByCode(Long code) {
        return productRepository.findByCode(code)
                .map(productMapper::toDto)
                .map(productDto -> new GenericResponse<>(HttpStatus.OK.value(), true, "Product found", productDto))
                .orElseThrow(() -> new ProductValidationException("Product with code " + code + " not found.", HttpStatus.NOT_FOUND));

    }

    @Transactional
    public GenericResponse<Void> changeProduct(ProductDto productDto) {

        var product = productRepository.findByCode(productDto.code())
                .orElseThrow(() -> new ProductValidationException("Product with code " + productDto.code() + " not found.", HttpStatus.NOT_FOUND));


        productMapper.updateEntityFromDto(productDto, product);
        log.info("Product '{}' changed with code '{}'", product.getProductName(), product.getCode());

        return new GenericResponse<>(HttpStatus.OK.value(), true, "Product changed successfully", null);
    }

    @Transactional
    public GenericResponse<Void> updateProduct(Long code, UpdateProductDto updateProductDto) {

        var product = productRepository.findByCode(code)
                .orElseThrow(() -> new ProductValidationException("Product with code " + code + " not found.", HttpStatus.NOT_FOUND));

        if (updateProductDto.productName() != null && !updateProductDto.productName().isBlank()) {
            product.setProductName(updateProductDto.productName());
        }

        if (updateProductDto.price() != null && updateProductDto.price() >= 0) {
            product.setPrice(updateProductDto.price());
        }

        if (updateProductDto.quantity() != null && updateProductDto.quantity() >= 0) {
            product.setQuantity(updateProductDto.quantity());
        }

        if (updateProductDto.description() != null && !updateProductDto.description().isBlank()) {
            product.setDescription(updateProductDto.description());
        }
        log.info("Product with code '{}' updated successfully", code);
        return new GenericResponse<>(HttpStatus.OK.value(), true, "Product updated successfully", null);
    }

    @Transactional
    public GenericResponse<Void> removeProduct(Long code) {
        if (!productRepository.existsByCode(code)) {
            throw new ProductValidationException("Product with code " + code + " does not exist.", HttpStatus.NOT_FOUND);
        }

        productRepository.deleteByCode(code);
        log.info("Product with code '{}' removed successfully", code);

        return new GenericResponse<>(HttpStatus.OK.value(), true, "Product removed successfully", null);
    }

    public GenericResponse<List<ProductDto>> getAllProducts() {
        List<ProductDto> products = productRepository.findAll()
                .stream()
                .map(productMapper::toDto)
                .toList();

        log.info("Retrieved {} products from the database", products.size());
        return new GenericResponse<>(HttpStatus.OK.value(), true, "Products retrieved successfully", products);
    }

    @Transactional
    public GenericResponse<Void> changeProductPrice(Long code, Double price) {
        if (price == null || price < 0) {
            throw new ProductValidationException("Price must be a non-negative value.", HttpStatus.BAD_REQUEST);
        }

        var product = productRepository.findByCode(code)
                .orElseThrow(() -> new ProductValidationException("Product with code " + code + " not found.", HttpStatus.NOT_FOUND));

        product.setPrice(price);
        log.info("Product with code '{}' price changed to '{}'", code, price);

        return new GenericResponse<>(HttpStatus.OK.value(), true, "Product price changed successfully", null);
    }
}
