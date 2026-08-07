package com.piorcode.cart_service.exception;

import java.util.UUID;

public class CartItemNotFoundException extends RuntimeException {
    
    public CartItemNotFoundException(UUID id) {
        super("Product with id: " + id + " not found in cart");
    }
}
