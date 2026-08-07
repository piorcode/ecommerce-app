package com.piorcode.cart_service.api;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record UpdateCartItemRequest(@Min(1) @Max(50) int quantity) {
}
