package com.piorcode.cart_service.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "carts")
public class CartEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<CartItemEntity> items = new ArrayList<>();

    public CartEntity() {

    }

    public CartEntity(UUID id, String userId, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<CartItemEntity> getItems() {
        return items;
    }

    public Optional<CartItemEntity> findByProductId(UUID productId) {
        return items
            .stream()
            .filter(item -> item.getProductId().equals(productId))
            .findFirst();
    }

    public void addItem(CartItemEntity item) {
        items.add(item);
        item.assignToCart(this);
    }

    public void removeItem(CartItemEntity item) {
        items.remove(item);
        item.assignToCart(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CartEntity that)) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
