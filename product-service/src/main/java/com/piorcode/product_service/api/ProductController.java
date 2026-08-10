package com.piorcode.product_service.api;

import com.piorcode.product_service.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @GetMapping("/{productId}")
    public ProductDetailsResponse getProductById(@PathVariable UUID productId) {
        return productService.findProductById(productId);
    }
    
    @GetMapping
    public List<ProductSummaryResponse> getAllProducts() {
        return productService.findAllProducts();
    }
}
