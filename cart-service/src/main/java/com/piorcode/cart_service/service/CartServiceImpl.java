package com.piorcode.cart_service.service;

import com.piorcode.cart_service.exception.CartItemNotFoundException;
import com.piorcode.cart_service.exception.CartNotFoundException;
import com.piorcode.cart_service.persistence.CartEntity;
import com.piorcode.cart_service.persistence.CartItemEntity;
import com.piorcode.cart_service.persistence.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    public CartServiceImpl(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    @Transactional
    public void addItem(String userId, UUID productId, int quantity) {
        CartEntity cartEntity = cartRepository
            .findByUserId(userId)
            .orElseGet(() -> new CartEntity(UUID.randomUUID(), userId, Instant.now()));

        cartEntity.findByProductId(productId)
            .ifPresentOrElse(item -> item.increaseQuantity(quantity),
                () -> {
                    CartItemEntity item = new CartItemEntity(UUID.randomUUID(), productId, quantity);
                    cartEntity.addItem(item);
                }
            );

        cartRepository.save(cartEntity);
    }

    @Override
    @Transactional
    public void updateItemQuantity(String userId, UUID productId, int quantity) {
        CartEntity cartEntity = cartRepository
            .findByUserId(userId)
            .orElseThrow(() -> new CartNotFoundException(userId));

        CartItemEntity cartItemEntity = cartEntity
            .findByProductId(productId)
            .orElseThrow(() -> new CartItemNotFoundException(productId));

        cartItemEntity.updateQuantity(quantity);

        cartRepository.save(cartEntity);
    }

    @Override
    @Transactional
    public void removeItem(String userId, UUID productId) {
        CartEntity cartEntity = cartRepository
            .findByUserId(userId)
            .orElseThrow(() -> new CartNotFoundException(userId));

        CartItemEntity cartItemEntity = cartEntity
            .findByProductId(productId)
            .orElseThrow(() -> new CartItemNotFoundException(productId));

        cartEntity.removeItem(cartItemEntity);

        cartRepository.save(cartEntity);
    }
}
