package com.example.springboot.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BrandDto(
        @NotNull
        UUID idBrand,
        @NotEmpty
        String nameBrand) {
}
