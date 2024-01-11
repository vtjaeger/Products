package com.example.springboot.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductDto(
        @NotBlank
        String name,
        @NotNull
        BigDecimal value,
        @NotBlank @NotEmpty @NotNull //xD
        String brand) {
}
