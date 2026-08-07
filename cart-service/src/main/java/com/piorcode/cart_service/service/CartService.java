package com.piorcode.cart_service.service;

import java.util.UUID;

public interface CartService {

    void addItem(String userId, UUID productId, int quantity);

    void updateItemQuantity(String userId, UUID productId, int quantity);

    void removeItem(String userId, UUID productId);
}
