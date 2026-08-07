package com.piorcode.product_service.service;

import com.piorcode.product_service.api.ProductDetailsResponse;
import com.piorcode.product_service.api.ProductSummaryResponse;
import com.piorcode.product_service.mapper.ProductMapper;
import com.piorcode.product_service.persistence.ProductEntity;
import com.piorcode.product_service.persistence.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension .class)
class ProductServiceImplTest {

    @Mock
    ProductRepository productRepository;
    
    ProductMapper productMapper;
    
    ProductService productService;

    @BeforeEach
    void setUp() {
        productMapper = new ProductMapper();
        productService = new ProductServiceImpl(productRepository, productMapper);
    }
    
    @Test
    void shouldReturnAllProducts() {
        // given
        List<ProductEntity> existingProducts = List.of(
            new ProductEntity(UUID.randomUUID(), "Laptop", "Basic laptop for everyday work.",
                BigDecimal.valueOf(3499.00), true, Instant.now()),
            new ProductEntity(UUID.randomUUID(), "Smartphone", "Modern smartphone with good battery life.",
                BigDecimal.valueOf(2499.00), true, Instant.now())
        );
        
        when(productRepository.findAll()).thenReturn(existingProducts);
        
        // when
        final List<ProductSummaryResponse> products = productService.findAllProducts();
        
        // then
        assertThat(products).hasSize(2);
        
        assertThat(products.get(0).name()).isEqualTo("Laptop");
        assertThat(products.get(0).price()).isEqualTo(BigDecimal.valueOf(3499.00));
        assertThat(products.get(0).available()).isTrue();

        assertThat(products.get(1).name()).isEqualTo("Smartphone");
        assertThat(products.get(1).price()).isEqualTo(BigDecimal.valueOf(2499.00));
        assertThat(products.get(1).available()).isTrue();
    }

    @Test
    void shouldReturnDetailsAboutSingleProduct() {
        // given
        UUID uuid = UUID.randomUUID();

        when(productRepository.findById(uuid)).thenReturn(Optional.of(
            new ProductEntity(uuid, "Laptop", "Basic laptop for everyday work.", BigDecimal.valueOf(3499.00), true,
                Instant.now())));

        // when
        final ProductDetailsResponse product = productService.findProductById(uuid);

        // then
        assertThat(product).isNotNull();
        assertThat(product.name()).isEqualTo("Laptop");
        assertThat(product.description()).isEqualTo("Basic laptop for everyday work.");
        assertThat(product.price()).isEqualTo(BigDecimal.valueOf(3499.00));
        assertThat(product.available()).isTrue();
        assertThat(product.createdAt()).isNotNull();
    }
}