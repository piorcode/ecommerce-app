package com.piorcode.cart_service.api;

import java.util.UUID;

public record CartItemResponse(UUID productId, int quantity) {
}
