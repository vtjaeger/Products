package com.example.springboot.services;

import com.example.springboot.models.BrandModel;
import com.example.springboot.repositories.BrandRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BrandService {
    @Autowired
    private BrandRepository brandRepository;

    @Transactional
    public BrandModel getOrCreateBrand(String nameBrand){
        BrandModel existingBrand = brandRepository.findByName(nameBrand);

        if (existingBrand == null) {
            BrandModel newBrand = new BrandModel(nameBrand);
            brandRepository.save(newBrand);
            return newBrand;
        } else {
            return existingBrand;
        }
    }

}
