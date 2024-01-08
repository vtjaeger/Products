package com.example.springboot.dtos;

import jakarta.validation.constraints.NotBlank;

public record BrandDto(@NotBlank String name) {
}
