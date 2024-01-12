package com.example.springboot.controllers;

import com.example.springboot.dtos.BrandDto;
import com.example.springboot.dtos.ProductDto;
import com.example.springboot.dtos.SaveProductResponseDto;
import com.example.springboot.models.BrandModel;
import com.example.springboot.models.ProductModel;
import com.example.springboot.repositories.BrandRepository;
import com.example.springboot.repositories.ProductRepository;
import com.example.springboot.services.BrandService;
import com.example.springboot.services.ProductService;
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
    @Autowired
    BrandService brandService;
    @Autowired
    ProductService productService;

    @PostMapping("/products")
    @Transactional
    public ResponseEntity<SaveProductResponseDto> saveProduct(@RequestBody @Valid ProductDto productDto){
        BrandModel existingBrand = brandService.getOrCreateBrand(productDto.brand());
        ProductModel productModel = productService.createProductModel(productDto, existingBrand);

        ProductModel savedProduct = productRepository.save(productModel);

        BrandDto brandDto = new BrandDto(productModel.getBrand().getIdBrand(), productModel.getBrand().getName());

        SaveProductResponseDto responseDto = new SaveProductResponseDto(savedProduct
                .getIdProduct(),
                savedProduct.getName(),
                savedProduct.getValue(),
                brandDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/products")
    public ResponseEntity<Page<ProductModel>> getProductsActive(@PageableDefault(page = 0, size = 10)Pageable pageable){
        Page<ProductModel> productModelPage = productRepository.findByActiveTrue(pageable);

        for (ProductModel productModel : productModelPage.getContent()) {
            UUID id = productModel.getIdProduct();
            productModel.add(linkTo(methodOn(ProductController.class).getOneProduct(id)).withSelfRel());
        }
        return ResponseEntity.status(HttpStatus.OK).body(productModelPage);
    }

    @GetMapping("/products/all")
    public ResponseEntity<Object> getAllProducts(@PageableDefault(page = 0, size = 10)Pageable pageable){
        Page<ProductModel> productModelPage = productRepository.findAll(pageable);

        for (ProductModel productModel : productModelPage.getContent()) {
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
        productModelOptional.get().add(linkTo(methodOn(ProductController.class).getProductsActive(Pageable.unpaged())).withSelfRel());
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
        var existingProduct = productModelOptional.get();
        var brand = brandService.getOrCreateBrand(productDto.brand());

        ProductModel updatedProduct = productService.updateProduct(existingProduct, productDto, brand);
        productRepository.save(updatedProduct);

        return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(updatedProduct));
    }

    @PatchMapping("/products/{id}")
    @Transactional
    public ResponseEntity<Object> activeOrInactiveProduct(@PathVariable(value = "id") UUID id){
        Optional<ProductModel> productModelOptional = productRepository.findById(id);
        if(productModelOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }

        ProductModel productModel = productModelOptional.get();
        String statusMessage;
        if(productModel.getActive()){
            productModel.disable();
            statusMessage = " (" + productModel.getBrand().getName() +  ") is now inactive.";
        } else {
            productModel.active();
            statusMessage = " (" + productModel.getBrand().getName() +  ") is now active.";
        }
        return ResponseEntity.status(HttpStatus.OK).body(productModel.getName() + statusMessage);
    }
}