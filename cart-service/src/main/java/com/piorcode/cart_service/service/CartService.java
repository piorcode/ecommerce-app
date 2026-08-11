package com.piorcode.cart_service.service;

import com.piorcode.cart_service.api.dto.CartResponse;

import java.util.UUID;

public interface CartService {

    CartResponse getCart(String userId);

    void addItem(String userId, UUID productId, int quantity);

    void updateItemQuantity(String userId, UUID productId, int quantity);

    void removeItem(String userId, UUID productId);
}
