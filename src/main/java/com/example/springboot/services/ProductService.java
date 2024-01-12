package com.example.springboot.services;

import com.example.springboot.dtos.ProductDto;
import com.example.springboot.models.BrandModel;
import com.example.springboot.models.ProductModel;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    public ProductModel createProductModel(ProductDto productDto, BrandModel brandModel){
        ProductModel productModel = new ProductModel();
        productModel.setBrand(brandModel);

        BeanUtils.copyProperties(productDto, productModel);
        return productModel;
    }

    public ProductModel updateProduct(ProductModel existingProduct, ProductDto productDto, BrandModel brandModel){
        existingProduct.setName(productDto.name());
        existingProduct.setValue(productDto.value());
        existingProduct.setBrand(brandModel);

        return existingProduct;
    }

    public ProductModel activeOrInactiveProduct(ProductModel productModel){
        if(productModel.getActive()){
            productModel.disable();
        } else {
            productModel.active();
        }
        return productModel;
    }
}