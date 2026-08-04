package com.piorcode.product_service.mapper;

import com.piorcode.product_service.api.ProductDetailsResponse;
import com.piorcode.product_service.api.ProductSummaryResponse;
import com.piorcode.product_service.persistence.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductDetailsResponse toProductDetailsResponse(ProductEntity productEntity) {
        return new ProductDetailsResponse(productEntity.getId(), productEntity.getName(),
            productEntity.getDescription(), productEntity.getPrice(), productEntity.isAvailable(),
            productEntity.getCreatedAt());
    }

    public ProductSummaryResponse toProductSummaryResponse(ProductEntity productEntity) {
        return new ProductSummaryResponse(productEntity.getId(), productEntity.getName(), productEntity.getPrice(),
            productEntity.isAvailable());
    }
}
