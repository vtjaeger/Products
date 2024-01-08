package com.example.springboot.controllers;

import com.example.springboot.models.BrandModel;
import com.example.springboot.models.ProductModel;
import com.example.springboot.repositories.BrandRepository;
import com.example.springboot.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("brands")
public class BrandController {
    @Autowired
    BrandRepository brandRepository;
    @Autowired
    ProductRepository productRepository;
    @GetMapping
    public ResponseEntity<Page<BrandModel>> getAllBrands(@PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable){
        Page<BrandModel> brandModelPage = brandRepository.findAll(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(brandModelPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getOneBrand(@PathVariable("id") UUID id){
        Optional<BrandModel> brandModelOptional = brandRepository.findById(id);
        if(brandModelOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Brand not found");
        }
        var brandModel = brandModelOptional.get();
        return ResponseEntity.status(HttpStatus.OK).body(brandModel);
    }

    @GetMapping("{id_brand}/products")
    public ResponseEntity<List<ProductModel>> getProductsByBrand(@PathVariable("id_brand") UUID brandId){
        Optional<BrandModel> brandModelOptional = brandRepository.findById(brandId);
        if(brandModelOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
        BrandModel brandModel = brandModelOptional.get();
        List<ProductModel> products = productRepository.findByBrand(brandModel);

        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

}
