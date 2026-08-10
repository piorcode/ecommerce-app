package com.piorcode.cart_service.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(UUID id, String name, String description, BigDecimal price, boolean available,
                              Instant createdAt) {
}
