package com.example.springboot.exceptions;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(UUID id, String resourceType){
        super(String.format("%s with ID: %s not found.", resourceType, id));
    }
}
