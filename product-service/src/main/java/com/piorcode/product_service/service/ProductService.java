package com.piorcode.product_service.service;

import com.piorcode.product_service.api.ProductDetailsResponse;
import com.piorcode.product_service.api.ProductSummaryResponse;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    
    ProductDetailsResponse findProductById(UUID productId);
    
    List<ProductSummaryResponse> findAllProducts();
}
