package com.piorcode.cart_service.exception;

public class ProductServiceUnavailableException extends RuntimeException {

    public ProductServiceUnavailableException() {
        super("Product service is unavailable");
    }
}



