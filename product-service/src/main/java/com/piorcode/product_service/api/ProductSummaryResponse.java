package com.piorcode.product_service.api;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductSummaryResponse(UUID id, String name, BigDecimal price, boolean available) {
}
