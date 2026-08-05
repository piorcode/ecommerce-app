package com.piorcode.cart_service.api;

import java.util.List;
import java.util.UUID;

public record CartResponse(UUID cartId, String userId, List<CartItemResponse> items) {
}
