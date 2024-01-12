package com.example.springboot.exceptions;

import com.example.springboot.models.BrandModel;

import java.util.UUID;

public class BrandNotFoundException extends RuntimeException{
    public BrandNotFoundException(UUID id){
        super("Brand with ID: " + id + " not found.");
    }
}
