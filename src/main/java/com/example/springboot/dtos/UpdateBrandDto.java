package com.example.springboot.dtos;

import jakarta.validation.constraints.NotBlank;

public record UpdateBrandDto(@NotBlank String name) {
}
