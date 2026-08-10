package com.piorcode.cart_service.api.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenRequest(@NotBlank String userId) {
}
