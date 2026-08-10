package com.piorcode.cart_service.api;

import com.piorcode.cart_service.api.dto.AddCartItemRequest;
import com.piorcode.cart_service.api.dto.UpdateCartItemRequest;
import com.piorcode.cart_service.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public void addItem(Authentication authentication, @Valid @RequestBody AddCartItemRequest request) {
        String userId = authentication.getName();
        cartService.addItem(userId, request.productId(), request.quantity());
    }

    @PatchMapping("/items/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateItem(Authentication authentication, @PathVariable UUID productId, 
        @Valid @RequestBody UpdateCartItemRequest request) {
        
        String userId = authentication.getName();
        cartService.updateItemQuantity(userId, productId, request.quantity());
    }

    @DeleteMapping("/items/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(Authentication authentication, @PathVariable UUID productId) {
        String userId = authentication.getName();
        cartService.removeItem(userId, productId);
    }
}
