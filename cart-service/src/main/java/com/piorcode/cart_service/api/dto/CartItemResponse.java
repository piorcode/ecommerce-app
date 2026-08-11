package com.piorcode.cart_service.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemResponse(UUID productId, String productName, BigDecimal price, int quantity ){
}