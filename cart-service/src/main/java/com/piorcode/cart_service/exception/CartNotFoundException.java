package com.piorcode.cart_service.exception;

public class CartNotFoundException extends RuntimeException {
    
    public CartNotFoundException(String id) {
        super("Cart not found for user: " + id);
    }
}
