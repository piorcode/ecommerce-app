package com.piorcode.cart_service.service;

import com.piorcode.cart_service.exception.CartItemNotFoundException;
import com.piorcode.cart_service.exception.CartNotFoundException;
import com.piorcode.cart_service.persistence.CartEntity;
import com.piorcode.cart_service.persistence.CartItemEntity;
import com.piorcode.cart_service.persistence.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {
    
    @Mock
    CartRepository cartRepository;
    
    CartService cartService;

    @BeforeEach
    void setUp() {
        cartService = new CartServiceImpl(cartRepository);
    }
    
    @Test
    void shouldCreateCartAndAddItemWhenCartDoesNotExist() {
        // given
        String user = "user";
        UUID productId = UUID.randomUUID();
        int quantity = 2;
        
        when(cartRepository.findByUserId(user)).thenReturn(Optional.empty());
        
        // when
        cartService.addItem(user, productId, quantity);
        
        // then
        ArgumentCaptor<CartEntity> cartCaptor = ArgumentCaptor.forClass(CartEntity.class);
        verify(cartRepository).save(cartCaptor.capture());
        CartEntity saved = cartCaptor.getValue();

        assertThat(saved).isExactlyInstanceOf(CartEntity.class);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUserId()).isEqualTo(user);
        assertThat(saved.getItems()).hasSize(1);
        assertThat(saved.getItems().get(0).getProductId()).isEqualTo(productId);
        assertThat(saved.getItems().get(0).getQuantity()).isEqualTo(quantity);
    }

    @Test
    void shouldIncreaseItemQuantityWhenInCart() {
        // given
        String user = "user";
        UUID productId = UUID.randomUUID();
        int existingQuantity = 2;
        int requestedQuantityToAdd = 3;
        
        CartEntity existingCart = new CartEntity(UUID.randomUUID(), user, Instant.now());
        existingCart.addItem(new CartItemEntity(UUID.randomUUID(), productId, existingQuantity));
        
        when(cartRepository.findByUserId(user)).thenReturn(Optional.of(existingCart));

        // when
        cartService.addItem(user, productId, requestedQuantityToAdd);

        // then
        ArgumentCaptor<CartEntity> cartCaptor = ArgumentCaptor.forClass(CartEntity.class);
        verify(cartRepository).save(cartCaptor.capture());
        CartEntity saved = cartCaptor.getValue();
        
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUserId()).isEqualTo(user);
        assertThat(saved.getItems()).hasSize(1);
        assertThat(saved.getItems().get(0).getProductId()).isEqualTo(productId);
        assertThat(saved.getItems().get(0).getQuantity()).isEqualTo(existingQuantity + requestedQuantityToAdd);
    }

    @Test
    void shouldUpdateItemQuantityWhenInCart() {
        // given
        String user = "user";
        UUID productId = UUID.randomUUID();
        int existingQuantity = 2;
        int requestedQuantityToReplace = 3;

        CartEntity existingCart = new CartEntity(UUID.randomUUID(), user, Instant.now());
        existingCart.addItem(new CartItemEntity(UUID.randomUUID(), productId, existingQuantity));

        when(cartRepository.findByUserId(user)).thenReturn(Optional.of(existingCart));

        // when
        cartService.updateItemQuantity(user, productId, requestedQuantityToReplace);

        // then
        ArgumentCaptor<CartEntity> cartCaptor = ArgumentCaptor.forClass(CartEntity.class);
        verify(cartRepository).save(cartCaptor.capture());
        CartEntity saved = cartCaptor.getValue();

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUserId()).isEqualTo(user);
        assertThat(saved.getItems()).hasSize(1);
        assertThat(saved.getItems().get(0).getProductId()).isEqualTo(productId);
        assertThat(saved.getItems().get(0).getQuantity()).isEqualTo(requestedQuantityToReplace);
    }

    @Test
    void shouldThrowExceptionWhenCartItemDoesNotExist() {
        // given
        String user = "user";
        UUID productId = UUID.randomUUID();
        int quantity = 2;

        when(cartRepository.findByUserId(user)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> cartService.updateItemQuantity(user, productId, quantity))
            .isInstanceOf(CartNotFoundException.class)
            .hasMessage("Cart not found for user: " + user);
    }

    @Test
    void shouldThrowExceptionWhenItemDoesNotExistInCart() {
        // given
        String user = "user";
        UUID productId = UUID.randomUUID();
        int quantity = 2;
        CartEntity existingCart = new CartEntity(UUID.randomUUID(), user, Instant.now());

        when(cartRepository.findByUserId(user)).thenReturn(Optional.of(existingCart));

        // when
        assertThatThrownBy(() -> cartService.updateItemQuantity(user, productId, quantity))
            .isInstanceOf(CartItemNotFoundException.class)
            .hasMessage("Product with id: " + productId + " not found in cart");
    }
    
    @Test
    void shouldRemoveItemWhenInCart() {
        // given
        String user = "user";
        UUID productId = UUID.randomUUID();
        int existingQuantity = 2;

        CartEntity existingCart = new CartEntity(UUID.randomUUID(), user, Instant.now());
        existingCart.addItem(new CartItemEntity(UUID.randomUUID(), productId, existingQuantity));

        when(cartRepository.findByUserId(user)).thenReturn(Optional.of(existingCart));

        // when
        cartService.removeItem(user, productId);

        // then
        ArgumentCaptor<CartEntity> cartCaptor = ArgumentCaptor.forClass(CartEntity.class);
        verify(cartRepository).save(cartCaptor.capture());
        CartEntity saved = cartCaptor.getValue();

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUserId()).isEqualTo(user);
        assertThat(saved.getItems()).isEmpty();
    }
}