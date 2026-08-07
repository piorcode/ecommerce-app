package com.piorcode.product_service.service;

import com.piorcode.product_service.api.ProductDetailsResponse;
import com.piorcode.product_service.api.ProductSummaryResponse;
import com.piorcode.product_service.exception.ProductNotFoundException;
import com.piorcode.product_service.mapper.ProductMapper;
import com.piorcode.product_service.persistence.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public ProductDetailsResponse findProductById(UUID productId) {
        return productRepository
            .findById(productId)
            .map(productMapper::toProductDetailsResponse)
            .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    @Override
    public List<ProductSummaryResponse> findAllProducts() {
        return productRepository.findAll().stream()
            .map(productMapper::toProductSummaryResponse)
            .toList();
    }
}
