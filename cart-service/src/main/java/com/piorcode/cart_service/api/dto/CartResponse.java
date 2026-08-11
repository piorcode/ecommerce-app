package com.piorcode.cart_service.api.dto;

import java.util.List;
import java.util.UUID;

public record CartResponse(UUID cartId, List<CartItemResponse> items) {
}

