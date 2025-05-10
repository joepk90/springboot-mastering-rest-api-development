package com.jparkkennaby.store.controllers;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.RestController;

import com.jparkkennaby.store.dtos.ProductDto;

import com.jparkkennaby.store.entities.Product;
import com.jparkkennaby.store.mappers.ProductMapper;
import com.jparkkennaby.store.repositories.ProductRepository;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@AllArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    
    @GetMapping
    public List<ProductDto> getAllProducts(
        @RequestParam(name = "categoryId", required = false) Byte categoryId
    ) {

        List<Product> products;
        if (categoryId != null) {
            products = productRepository.findByCategoryId(categoryId);
        } else {
            products = productRepository.findAllWithCategory();
        }
        
        return products
            .stream()
            .map(productMapper::toDto)
            .toList();
    }

     @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        var product = productRepository.findById(id).orElse(null);
        if (product == null) {
            // return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return ResponseEntity.notFound().build();
        }
        
         // return new ResponseEntity<>(user, HttpStatus.OK);
         return ResponseEntity.ok(productMapper.toDto(product));
    }
    
}
