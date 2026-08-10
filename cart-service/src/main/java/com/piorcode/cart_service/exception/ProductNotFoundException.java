package com.piorcode.cart_service.exception;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {
    
    public ProductNotFoundException(UUID id) {
        super("Product with id " + id + " was not found");
    }
}
