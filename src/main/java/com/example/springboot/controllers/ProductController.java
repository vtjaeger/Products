package com.example.springboot.controllers;

import com.example.springboot.dtos.ProductDto;
import com.example.springboot.models.BrandModel;
import com.example.springboot.models.ProductModel;
import com.example.springboot.repositories.BrandRepository;
import com.example.springboot.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class ProductController {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    BrandRepository brandRepository;

    @PostMapping("/products")
    @Transactional
    public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductDto productDto){
        var productModel = new ProductModel();

        String brandName = productDto.brand();
        BrandModel existingBrand = brandRepository.findByName(brandName);
        if(existingBrand == null){
            BrandModel brandModel = new BrandModel(brandName);
            brandRepository.save(brandModel);
            productModel.setBrand(brandModel);
        } else {
            productModel.setBrand(existingBrand);

        }
        BeanUtils.copyProperties(productDto, productModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
    }

    @GetMapping("/products")
    public ResponseEntity<Page<ProductModel>> getAllProducts(@PageableDefault(page = 0, size = 10)Pageable pageable){
        Page<ProductModel> productModelPage = productRepository.findAll(pageable);
        for(ProductModel productModel : productModelPage.getContent()){
            UUID id = productModel.getIdProduct();
            productModel.add(linkTo(methodOn(ProductController.class).getOneProduct(id)).withSelfRel());
        }
        return ResponseEntity.status(HttpStatus.OK).body(productModelPage);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Object> getOneProduct(@PathVariable(value = "id")UUID id){
        Optional<ProductModel> productModelOptional = productRepository.findById(id);
        if(productModelOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
        productModelOptional.get().add(linkTo(methodOn(ProductController.class).getAllProducts(Pageable.unpaged())).withSelfRel());
        return ResponseEntity.status(HttpStatus.OK).body(productModelOptional.get());
    }

    @PutMapping("/products/{id}")
    @Transactional
    public ResponseEntity<Object> updateProduct(@PathVariable(value = "id") UUID id,
                                                @RequestBody @Valid ProductDto productDto){
        Optional<ProductModel> productModelOptional = productRepository.findById(id);
        if(productModelOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
        var productModel = productModelOptional.get();

        String brandName = productDto.brand();
        BrandModel brandModel  = brandRepository.findByName(brandName);

        if (brandModel == null) {
            brandModel = new BrandModel(brandName);
            brandModel = brandRepository.save(brandModel);
        }

        BeanUtils.copyProperties(productDto, productModel);
        productModel.setBrand(brandModel);
        return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModel));
    }

    @DeleteMapping("/products/{id}")
    @Transactional
    public ResponseEntity<Object> deleteProduct(@PathVariable(value = "id") UUID id){
        Optional<ProductModel> productModelOptional = productRepository.findById(id);
        if(productModelOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
        productRepository.delete(productModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully");
    }
}