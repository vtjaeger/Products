package com.example.springboot.dtos;

import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.UUID;

public record SaveProductResponseDto(
        UUID idProduct,
        String productName,
        BigDecimal value,
        @Valid
        BrandDto brandDto) {
}