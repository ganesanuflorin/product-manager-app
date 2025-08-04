package com.product.manager.service;

import com.product.manager.dto.ProductDto;
import com.product.manager.dto.UpdateProductDto;
import com.product.manager.entity.Product;
import com.product.manager.exceptions.ProductValidationException;
import com.product.manager.mapper.ProductMapper;
import com.product.manager.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;


    @Test
    void testAddProduct_Success() {
        ProductDto dto = new ProductDto("Test", 10.0, 5L, "Test desc", 1L);
        Product entity = new Product();
        when(productRepository.existsByCode(1L)).thenReturn(false);
        when(productMapper.toEntity(dto)).thenReturn(entity);

        var response = productService.addProduct(dto);

        assertEquals(HttpStatus.CREATED.value(), response.status());
    }

    @Test
    void testAddProduct_AlreadyExists() {
        ProductDto dto = new ProductDto("Test", 10.0, 5L, "Test desc", 1L);
        when(productRepository.existsByCode(1L)).thenReturn(true);

        var exception = assertThrows(ProductValidationException.class,
                () -> productService.addProduct(dto));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void testGetProductByCode_Found() {
        Product product = new Product();
        ProductDto dto = new ProductDto("Test", 10.0, 5L, "Test desc", 1L);
        when(productRepository.findByCode(1L)).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(dto);

        var response = productService.getProductByCode(1L);

        assertTrue(response.success());
        assertEquals(dto, response.data());
    }

    @Test
    void testGetProductByCode_NotFound() {
        when(productRepository.findByCode(1L)).thenReturn(Optional.empty());

        var exception = assertThrows(ProductValidationException.class,
                () -> productService.getProductByCode(1L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

    }

    @Test
    void testChangeProduct_Success() {
        ProductDto dto = new ProductDto("Test", 10.0, 5L, "Test desc", 1L);

        Product product = new Product();
        product.setCode(1L);
        product.setProductName("Old Name");
        product.setPrice(5.0);
        product.setQuantity(2L);
        product.setDescription("Old Desc");

        when(productRepository.findByCode(1L)).thenReturn(Optional.of(product));
        doAnswer(invocation -> {
            ProductDto src = invocation.getArgument(0);
            Product target = invocation.getArgument(1);
            target.setProductName(src.productName());
            target.setPrice(src.price());
            target.setQuantity(src.quantity());
            target.setDescription(src.description());
            return null;
        }).when(productMapper).updateEntityFromDto(dto, product);


        var response = productService.changeProduct(dto);

        verify(productMapper).updateEntityFromDto(dto, product);
        assertEquals(HttpStatus.OK.value(), response.status());
        assertEquals(dto.productName(), product.getProductName());
        assertEquals(dto.price(), product.getPrice());
        assertEquals(dto.quantity(), product.getQuantity());
        assertEquals(dto.description(), product.getDescription());
    }

    @Test
    void testChangeProduct_NotFound() {
        ProductDto dto = new ProductDto("Test", 10.0, 5L, "Test desc",1L);
        when(productRepository.findByCode(1L)).thenReturn(Optional.empty());

        var exception = assertThrows(ProductValidationException.class,
                () -> productService.changeProduct(dto));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

    }

    @Test
    void testUpdateProduct_Success() {
        UpdateProductDto update = new UpdateProductDto(null, 99.9, 20L, null);
        Product product = new Product();
        product.setCode(1L);
        product.setPrice(50.0);
        product.setQuantity(10L);
        when(productRepository.findByCode(1L)).thenReturn(Optional.of(product));
        var response = productService.updateProduct(1L, update);

        assertEquals(update.price(), product.getPrice());
        assertEquals(update.quantity(), product.getQuantity());
        assertEquals(HttpStatus.OK.value(), response.status());
    }

    @Test
    void testUpdateProduct_NotFound() {
        UpdateProductDto update = new UpdateProductDto(null, 99.9, 20L, null);
        when(productRepository.findByCode(1L)).thenReturn(Optional.empty());

        assertThrows(ProductValidationException.class,
                () -> productService.updateProduct(1L, update));
    }

    @Test
    void testRemoveProduct_Success() {
        when(productRepository.existsByCode(1L)).thenReturn(true);

        var response = productService.removeProduct(1L);

        verify(productRepository).deleteByCode(1L);
        assertEquals(HttpStatus.OK.value(), response.status());
    }

    @Test
    void testRemoveProduct_NotFound() {
        when(productRepository.existsByCode(1L)).thenReturn(false);

        var exception = assertThrows(ProductValidationException.class,
                () -> productService.removeProduct(1L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testChangeProductPrice_Success() {
        Long code = 1L;
        Double newPrice = 99.99;
        Product product = new Product();
        product.setCode(code);
        product.setPrice(50.0);

        when(productRepository.findByCode(code)).thenReturn(Optional.of(product));

        var response = productService.changeProductPrice(code, newPrice);

        assertEquals(HttpStatus.OK.value(), response.status());
        assertTrue(response.success());
        assertEquals("Product price changed successfully", response.message());
        assertEquals(newPrice, product.getPrice());
    }

    @Test
    void testChangeProductPrice_NullPrice_ShouldThrow() {
        Long code = 1L;

        var ex = assertThrows(ProductValidationException.class, () ->
                productService.changeProductPrice(code, null)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertTrue(ex.getMessage().contains("non-negative"));
    }

    @Test
    void testChangeProductPrice_NegativePrice_ShouldThrow() {
        Long code = 1L;

        var ex = assertThrows(ProductValidationException.class, () ->
                productService.changeProductPrice(code, -10.0)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertTrue(ex.getMessage().contains("non-negative"));
    }


    @Test
    void testChangeProductPrice_ProductNotFound_ShouldThrow() {
        Long code = 999L;
        Double price = 10.0;

        when(productRepository.findByCode(code)).thenReturn(Optional.empty());

        var ex = assertThrows(ProductValidationException.class, () ->
                productService.changeProductPrice(code, price)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    void testGetAllProducts_Success() {
        Product product1 = new Product(1L, "Test", 10.0, 5L, "Desc", 1L);
        Product product2 = new Product(2L, "Test", 10.0, 5L, "Desc", 2L);
        Product product3 = new Product(3L, "Test", 10.0, 5L, "Desc", 3L);
        ProductDto productDto1 = new ProductDto("Test", 10.0, 5L, "Desc", 1L);
        ProductDto productDto2 = new ProductDto("Test", 10.0, 5L, "Desc", 2L);
        ProductDto productDto3 = new ProductDto("Test", 10.0, 5L, "Desc", 3L);
        when(productMapper.toDto(product1)).thenReturn(productDto1);
        when(productMapper.toDto(product2)).thenReturn(productDto2);
        when(productMapper.toDto(product3)).thenReturn(productDto3);
        when(productRepository.findAll()).thenReturn(List.of(product1, product2, product3));

        var response = productService.getAllProducts();

        assertEquals(3, response.data().size());
        assertEquals(productDto1, response.data().getFirst());
    }
}
