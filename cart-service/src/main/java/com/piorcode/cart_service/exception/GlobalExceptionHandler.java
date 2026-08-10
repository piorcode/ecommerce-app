package com.piorcode.cart_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CartItemNotFoundException.class)
    public ProblemDetail handleCartItemNotFound(CartItemNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Cart item not found");
        problem.setDetail(ex.getMessage());

        return problem;
    }

    @ExceptionHandler(CartNotFoundException.class)
    public ProblemDetail handleCartNotFound(CartNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Cart not found");
        problem.setDetail(ex.getMessage());

        return problem;
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ProblemDetail handleProductNotFound(ProductNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Product not found");
        problem.setDetail(ex.getMessage());

        return problem;
    }

    @ExceptionHandler(ProductServiceUnavailableException.class)
    public ProblemDetail handleProductServiceUnavailable(ProductServiceUnavailableException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.SERVICE_UNAVAILABLE);
        problem.setTitle("Product service unavailable");
        problem.setDetail(ex.getMessage());

        return problem;
    }

    @ExceptionHandler(ProductUnavailableException.class)
    public ProblemDetail handleProductUnavailable(ProductUnavailableException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problem.setTitle("Product unavailable");
        problem.setDetail(ex.getMessage());

        return problem;
    }
}