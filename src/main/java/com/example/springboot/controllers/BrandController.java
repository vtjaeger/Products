package com.example.springboot.controllers;

import com.example.springboot.dtos.UpdateBrandDto;
import com.example.springboot.exceptions.ResourceNotFoundException;
import com.example.springboot.models.BrandModel;
import com.example.springboot.models.ProductModel;
import com.example.springboot.repositories.BrandRepository;
import com.example.springboot.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("brands")
public class BrandController {
    @Autowired
    BrandRepository brandRepository;
    @Autowired
    ProductRepository productRepository;
    @GetMapping
    public ResponseEntity<Page<BrandModel>> getBrandsActive(@PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable){
        Page<BrandModel> brandModelPage = brandRepository.findByActiveTrue(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(brandModelPage);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<BrandModel>> getAllBrands(@PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable){
        Page<BrandModel> brandModelPage = brandRepository.findAll(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(brandModelPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getOneBrand(@PathVariable("id") UUID id){
        Optional<BrandModel> brandModelOptional = brandRepository.findById(id);
        if(brandModelOptional.isEmpty()){
            throw new ResourceNotFoundException(id, "Brand");
        }

        var brandModel = brandModelOptional.get();
        return ResponseEntity.status(HttpStatus.OK).body(brandModel);
    }

    @GetMapping("{id_brand}/products")
    public ResponseEntity<List<ProductModel>> getProductsByBrand(@PathVariable("id_brand") UUID id){
        Optional<BrandModel> brandModelOptional = brandRepository.findById(id);
        if(brandModelOptional.isEmpty()){
            throw new ResourceNotFoundException(id, "Brand");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
        BrandModel brandModel = brandModelOptional.get();
        List<ProductModel> products = productRepository.findByBrand(brandModel);

        for (ProductModel product : products){
            UUID productID = product.getIdProduct();
            product.add(linkTo(methodOn(ProductController.class).getOneProduct(productID)).withSelfRel());
        }

        //OUTRA FORMA
        // List<ProductModel> products = productRepository.findByBrand(brandModel);
        //products.forEach(product -> product.add(linkTo(methodOn(ProductController.class).getOneProduct(product.getIdProduct())).withSelfRel()));

        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

//    @DeleteMapping("/{id}")
//    @Transactional
//    public ResponseEntity<Object> deleteBrand(@PathVariable(value = "id") UUID id){
//        Optional<BrandModel> brandModelOptional = brandRepository.findById(id);
//        if (brandModelOptional.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Brand not found");
//        }
//        BrandModel brandModel = brandModelOptional.get();
//        brandModel.setActive(false);
//        brandRepository.save(brandModel);
//
//        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//    }

    @PatchMapping("/{id}")
    @Transactional
    public ResponseEntity<Object> activeOrInactive(@PathVariable("id") UUID id){
        var brand = brandRepository.getReferenceById(id);
        String statusMessage;

        if(brand.getActive()){
            brand.inactive();
            statusMessage = " is now inactive";
        } else {
            brand.active();
            statusMessage = " is now active";
        }
        return ResponseEntity.status(HttpStatus.OK).body(brand.getName() + statusMessage);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Object> updateBrand(@PathVariable("id") UUID id, @RequestBody @Valid UpdateBrandDto brandDto){
        Optional<BrandModel> brandModelOptional = brandRepository.findById(id);
        if(brandModelOptional.isEmpty()){
            throw new ResourceNotFoundException(id, "Brand");
        }
        var existingBrand = brandModelOptional.get();
        existingBrand.setName(brandDto.name());

        brandRepository.save(existingBrand);
        return ResponseEntity.status(HttpStatus.OK).body(existingBrand);
    }

}
