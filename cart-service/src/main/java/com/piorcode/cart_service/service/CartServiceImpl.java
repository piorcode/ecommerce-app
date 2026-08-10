package com.piorcode.cart_service.service;

import com.piorcode.cart_service.api.ProductResponse;
import com.piorcode.cart_service.client.ProductServiceClient;
import com.piorcode.cart_service.exception.CartItemNotFoundException;
import com.piorcode.cart_service.exception.CartNotFoundException;
import com.piorcode.cart_service.exception.ProductUnavailableException;
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
    
    private final ProductServiceClient productServiceClient;

    public CartServiceImpl(CartRepository cartRepository, ProductServiceClient productServiceClient) {
        this.cartRepository = cartRepository;
        this.productServiceClient = productServiceClient;
    }

    @Override
    @Transactional
    public void addItem(String userId, UUID productId, int quantity) {
        ProductResponse product = productServiceClient.getProduct(productId);

        if (!product.available()) {
            throw new ProductUnavailableException(product.id());
        }
        
        CartEntity cartEntity = getOrCreateCart(userId);

        cartEntity
            .findByProductId(product.id())
            .ifPresentOrElse(item -> item.increaseQuantity(quantity),
                () -> {
                    CartItemEntity item = new CartItemEntity(UUID.randomUUID(), product.id(), quantity);
                    cartEntity.addItem(item);
                }
            );

        cartRepository.save(cartEntity);
    }

    @Override
    @Transactional
    public void updateItemQuantity(String userId, UUID productId, int quantity) {
        CartEntity cartEntity = getExistingCart(userId);
        CartItemEntity cartItemEntity = findItemInCart(productId, cartEntity);
        cartItemEntity.updateQuantity(quantity);
        cartRepository.save(cartEntity);
    }

    @Override
    @Transactional
    public void removeItem(String userId, UUID productId) {
        CartEntity cartEntity = getExistingCart(userId);
        CartItemEntity cartItemEntity = findItemInCart(productId, cartEntity);
        cartEntity.removeItem(cartItemEntity);
        cartRepository.save(cartEntity);
    }

    private CartEntity getOrCreateCart(String userId) {
        return cartRepository
            .findByUserId(userId)
            .orElseGet(() -> new CartEntity(UUID.randomUUID(), userId, Instant.now()));
    }

    private CartEntity getExistingCart(String userId) {
        return cartRepository
            .findByUserId(userId)
            .orElseThrow(() -> new CartNotFoundException(userId));
    }

    private CartItemEntity findItemInCart(UUID productId, CartEntity cartEntity) {
        return cartEntity
            .findByProductId(productId)
            .orElseThrow(() -> new CartItemNotFoundException(productId));
    }
}
