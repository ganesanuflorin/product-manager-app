package com.product.manager.controller;

import com.product.manager.dto.GenericResponse;
import com.product.manager.dto.ProductDto;
import com.product.manager.mapper.ProductMapper;
import com.product.manager.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class ProductControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    @LocalServerPort
    private int port;

    private HttpHeaders adminHeaders;
    private HttpHeaders userHeaders;

    private Long testProductCode;

    @BeforeEach
    @Transactional
    void setUp() {
        adminHeaders = getAuthHeaders("admin", "admin123");
        userHeaders = getAuthHeaders("user", "user123");
    }

    @AfterEach
    void tearDown() {
    }

    private HttpHeaders getAuthHeaders(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String loginPayload = String.format("""
                    {
                      "username": "%s",
                      "password": "%s"
                    }
                """, username, password);

        HttpEntity<String> loginRequest = new HttpEntity<>(loginPayload, headers);
        ResponseEntity<GenericResponse<String>> loginResponse = restTemplate.exchange(
                "http://localhost:" + port + "/api/v1/auth/login",
                HttpMethod.POST,
                loginRequest,
                new ParameterizedTypeReference<GenericResponse<String>>() {
                });

        String token = Objects.requireNonNull(loginResponse.getBody()).data();

        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setContentType(MediaType.APPLICATION_JSON);
        authHeaders.setBearerAuth(token);
        return authHeaders;
    }

    @Test
    void shouldAddProductWithAdminRole() {
        testProductCode = 1001L;

        ProductDto product = new ProductDto("SSD 1TB", 129.99, 10L, "Fast storage", testProductCode);

        HttpEntity<ProductDto> request = new HttpEntity<>(product, adminHeaders);

        ResponseEntity<GenericResponse<?>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/v1/product/add",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<GenericResponse<?>>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().success());

        // Optionally verify DB
        assertTrue(productRepository.existsByCode(product.code()));
    }

    @Test
    void shouldGetProductByCodeWithUserRole() {
        testProductCode = 1001L;

        ProductDto product = new ProductDto("HDD 2TB", 89.99, 5L, "Reliable storage", testProductCode);

        productRepository.save(productMapper.toEntity(product));

        HttpEntity<Void> request = new HttpEntity<>(userHeaders);

        ResponseEntity<GenericResponse<ProductDto>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/v1/product/" + testProductCode,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<GenericResponse<ProductDto>>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().success());
        assertEquals(product, response.getBody().data());
    }

    @Test
    void shouldChangeProductWithAdminRole() {
        testProductCode = 1001L;
        ProductDto product = new ProductDto("SSD 1TB", 129.99, 10L, "Fast storage", testProductCode);
        productRepository.save(productMapper.toEntity(product));
        ProductDto updatedProduct = new ProductDto("SSD 1TB Pro1", 149.99, 8L, "Faster storage2", testProductCode);
        HttpEntity<ProductDto> request = new HttpEntity<>(updatedProduct, adminHeaders);
        ResponseEntity<GenericResponse<?>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/v1/product/change",
                HttpMethod.PUT,
                request,
                new ParameterizedTypeReference<GenericResponse<?>>() {
                });
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().success());
        ProductDto fetchedProduct = productMapper.toDto(productRepository.findByCode(testProductCode).orElseThrow());
        assertEquals(updatedProduct.productName(), fetchedProduct.productName());
        assertEquals(updatedProduct.price(), fetchedProduct.price());
        assertEquals(updatedProduct.quantity(), fetchedProduct.quantity());
        assertEquals(updatedProduct.description(), fetchedProduct.description());
    }

    @Test
    void shouldDeleteProductWithAdminRole() {
        testProductCode = 1001L;
        ProductDto product = new ProductDto("SSD 1TB", 129.99, 10L, "Fast storage", testProductCode);
        productRepository.save(productMapper.toEntity(product));

        HttpEntity<Void> request = new HttpEntity<>(adminHeaders);

        ResponseEntity<GenericResponse<?>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/v1/product/" + testProductCode,
                HttpMethod.DELETE,
                request,
                new ParameterizedTypeReference<GenericResponse<?>>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().success());
        assertTrue(productRepository.findByCode(testProductCode).isEmpty());
    }

    @Test
    void shouldToRetrieveAllProductsWithUserRole() {
        ProductDto product1 = new ProductDto("SSD 1TB", 129.99, 10L, "Fast storage", 1001L);
        ProductDto product2 = new ProductDto("HDD 2TB", 89.99, 5L, "Reliable storage", 1002L);
        productRepository.save(productMapper.toEntity(product1));
        productRepository.save(productMapper.toEntity(product2));

        HttpEntity<Void> request = new HttpEntity<>(userHeaders);

        ResponseEntity<GenericResponse<List<ProductDto>>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/v1/product/list",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<GenericResponse<List<ProductDto>>>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().success());
        assertEquals(2, response.getBody().data().size());
    }

    @Test
    void shouldChangeProductPriceWithAdminRole() {
        testProductCode = 1001L;
        ProductDto product = new ProductDto("SSD 1TB", 129.99, 10L, "Fast storage", testProductCode);
        productRepository.save(productMapper.toEntity(product));

        double newPrice = 139.99;
        HttpEntity<Void> request = new HttpEntity<>(adminHeaders);

        ResponseEntity<GenericResponse<?>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/v1/product/" + testProductCode + "/change/" + newPrice,
                HttpMethod.PUT,
                request,
                new ParameterizedTypeReference<GenericResponse<?>>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().success());

        ProductDto updatedProduct = productMapper.toDto(productRepository.findByCode(testProductCode).orElseThrow());
        assertEquals(newPrice, updatedProduct.price());
    }
}
