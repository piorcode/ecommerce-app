package com.piorcode.cart_service.service;

import com.piorcode.cart_service.api.dto.CartItemResponse;
import com.piorcode.cart_service.api.dto.CartResponse;
import com.piorcode.cart_service.api.dto.ProductResponse;
import com.piorcode.cart_service.client.ProductServiceClient;
import com.piorcode.cart_service.exception.CartItemNotFoundException;
import com.piorcode.cart_service.exception.CartNotFoundException;
import com.piorcode.cart_service.exception.ProductUnavailableException;
import com.piorcode.cart_service.persistence.CartEntity;
import com.piorcode.cart_service.persistence.CartItemEntity;
import com.piorcode.cart_service.persistence.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    private static final String USER_ID = "user";
    
    private static final int QUANTITY = 2;

    private static final String PRODUCT_NAME = "Product Name";

    private static final BigDecimal ITEM_PRICE = new BigDecimal("10.0");

    private static final String PRODUCT_DESCRIPTION = "Product Description";

    @Mock
    CartRepository cartRepository;

    @Mock
    ProductServiceClient productServiceClient;

    CartService cartService;

    @BeforeEach
    void setUp() {
        cartService = new CartServiceImpl(cartRepository, productServiceClient);
    }

    @Test
    void shouldReturnCartForUser() {
        // given
        UUID productId = UUID.randomUUID();

        CartEntity existingCart = new CartEntity(UUID.randomUUID(), USER_ID, Instant.now());
        existingCart.addItem(getCartItem(productId));

        when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(existingCart));

        // when
        CartResponse response = cartService.getCart(USER_ID);

        // then
        assertThat(response).isNotNull();
        assertThat(response.cartId()).isEqualTo(existingCart.getId());
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).productId()).isEqualTo(productId);
        assertThat(response.items().get(0).productName()).isEqualTo(PRODUCT_NAME);
        assertThat(response.items().get(0).price()).isEqualTo(ITEM_PRICE);
        assertThat(response.items().get(0).quantity()).isEqualTo(QUANTITY);

        verify(cartRepository).findByUserId(USER_ID);
    }

    @Test
    void shouldCreateCartAndAddAvailableItemWhenCartDoesNotExist() {
        // given
        UUID productId = UUID.randomUUID();

        when(productServiceClient.getProduct(productId)).thenReturn(getProduct(productId, true));
        when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        // when
        cartService.addItem(USER_ID, productId, QUANTITY);

        // then
        ArgumentCaptor<CartEntity> cartCaptor = ArgumentCaptor.forClass(CartEntity.class);
        verify(cartRepository).save(cartCaptor.capture());
        CartEntity saved = cartCaptor.getValue();

        assertThat(saved).isExactlyInstanceOf(CartEntity.class);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUserId()).isEqualTo(USER_ID);
        assertThat(saved.getItems()).hasSize(1);
        assertThat(saved.getItems().get(0).getProductId()).isEqualTo(productId);
        assertThat(saved.getItems().get(0).getQuantity()).isEqualTo(QUANTITY);
    }

    @Test
    void shouldThrowExceptionWhenProductIsUnavailable() {
        // given
        UUID productId = UUID.randomUUID();

        when(productServiceClient.getProduct(productId)).thenReturn(getProduct(productId, false));

        // when
        assertThatThrownBy(() -> cartService.addItem(USER_ID, productId, QUANTITY))
            .isInstanceOf(ProductUnavailableException.class)
            .hasMessage("Product with id: " + productId + " is unavailable");
    }

    @Test
    void shouldIncreaseItemQuantityWhenInCart() {
        // given
        UUID productId = UUID.randomUUID();
        int requestedQuantityToAdd = 3;

        CartEntity existingCart = new CartEntity(UUID.randomUUID(), USER_ID, Instant.now());
        existingCart.addItem(getCartItem(productId));

        when(productServiceClient.getProduct(productId)).thenReturn(getProduct(productId, true));
        when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(existingCart));

        // when
        cartService.addItem(USER_ID, productId, requestedQuantityToAdd);

        // then
        assertThat(existingCart.getId()).isNotNull();
        assertThat(existingCart.getUserId()).isEqualTo(USER_ID);
        assertThat(existingCart.getItems()).hasSize(1);
        assertThat(existingCart.getItems().get(0).getProductId()).isEqualTo(productId);
        assertThat(existingCart.getItems().get(0).getProductName()).isEqualTo(PRODUCT_NAME);
        assertThat(existingCart.getItems().get(0).getPrice()).isEqualTo(ITEM_PRICE);
        assertThat(existingCart.getItems().get(0).getQuantity()).isEqualTo(QUANTITY + requestedQuantityToAdd);

        verify(cartRepository).save(existingCart);
    }

    @Test
    void shouldUpdateItemQuantityWhenInCart() {
        // given
        UUID productId = UUID.randomUUID();
        int requestedQuantityToReplace = 3;

        CartEntity existingCart = new CartEntity(UUID.randomUUID(), USER_ID, Instant.now());
        existingCart.addItem(getCartItem(productId));

        when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(existingCart));

        // when
        cartService.updateItemQuantity(USER_ID, productId, requestedQuantityToReplace);

        // then
        assertThat(existingCart.getId()).isNotNull();
        assertThat(existingCart.getItems()).hasSize(1);
        assertThat(existingCart.getItems().get(0).getProductId()).isEqualTo(productId);
        assertThat(existingCart.getItems().get(0).getProductName()).isEqualTo(PRODUCT_NAME);
        assertThat(existingCart.getItems().get(0).getPrice()).isEqualTo(ITEM_PRICE);
        assertThat(existingCart.getItems().get(0).getQuantity()).isEqualTo(requestedQuantityToReplace);

        verify(cartRepository).save(existingCart);
    }

    @Test
    void shouldThrowExceptionWhenCartItemDoesNotExist() {
        // given
        UUID productId = UUID.randomUUID();

        when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> cartService.updateItemQuantity(USER_ID, productId, QUANTITY))
            .isInstanceOf(CartNotFoundException.class)
            .hasMessage("Cart not found for user: " + USER_ID);
    }

    @Test
    void shouldThrowExceptionWhenItemDoesNotExistInCart() {
        // given
        UUID productId = UUID.randomUUID();
        CartEntity existingCart = new CartEntity(UUID.randomUUID(), USER_ID, Instant.now());

        when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(existingCart));

        // when
        assertThatThrownBy(() -> cartService.updateItemQuantity(USER_ID, productId, QUANTITY))
            .isInstanceOf(CartItemNotFoundException.class)
            .hasMessage("Product with id: " + productId + " not found in cart");
    }

    @Test
    void shouldRemoveItemWhenInCart() {
        // given
        UUID productId = UUID.randomUUID();

        CartEntity existingCart = new CartEntity(UUID.randomUUID(), USER_ID, Instant.now());
        existingCart.addItem(getCartItem(productId));

        when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(existingCart));

        // when
        cartService.removeItem(USER_ID, productId);

        // then
        assertThat(existingCart.getId()).isNotNull();
        assertThat(existingCart.getUserId()).isEqualTo(USER_ID);
        assertThat(existingCart.getItems()).isEmpty();
    }

    private ProductResponse getProduct(UUID productId, boolean available) {
        return new ProductResponse(
            productId,
            PRODUCT_NAME,
            PRODUCT_DESCRIPTION,
            ITEM_PRICE,
            available,
            Instant.now());
    }

    private CartItemEntity getCartItem(UUID productId) {
        return new CartItemEntity(UUID.randomUUID(), productId, PRODUCT_NAME, ITEM_PRICE, QUANTITY);
    }
}