package com.example.springboot.services;

import com.example.springboot.dtos.BrandDto;
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
}
