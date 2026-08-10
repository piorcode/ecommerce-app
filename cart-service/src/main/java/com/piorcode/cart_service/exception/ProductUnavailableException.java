package com.piorcode.cart_service.exception;

import java.util.UUID;

public class ProductUnavailableException extends RuntimeException {
    
    public ProductUnavailableException(UUID id) {
        super("Product with id: " + id + " is unavailable");
    }
}
