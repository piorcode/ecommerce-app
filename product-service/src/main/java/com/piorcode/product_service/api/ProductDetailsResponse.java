package com.piorcode.product_service.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class ProductDetailsResponse {

    private final UUID id;
    
    private final String name;

    private final String description;

    private final BigDecimal price;

    private final boolean available;

    private final Instant createdAt;

    public ProductDetailsResponse(UUID id, String name, String description, BigDecimal price, boolean available, 
        Instant createdAt) {
        
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.available = available;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public boolean isAvailable() {
        return available;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
