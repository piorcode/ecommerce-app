package com.piorcode.product_service.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductDetailsResponse(UUID id, String name, String description, BigDecimal price, boolean available,
                                     Instant createdAt) {
}
