package com.piorcode.product_service.api;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductSummaryResponse {

    private final UUID id;
    
    private final String name;

    private final BigDecimal price;

    private final boolean available;

    public ProductSummaryResponse(UUID id, String name, BigDecimal price, boolean available) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.available = available;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public boolean isAvailable() {
        return available;
    }
}
