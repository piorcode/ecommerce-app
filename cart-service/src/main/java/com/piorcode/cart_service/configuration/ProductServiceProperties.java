package com.piorcode.cart_service.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "product-service")
public record ProductServiceProperties(String baseUrl) {
}