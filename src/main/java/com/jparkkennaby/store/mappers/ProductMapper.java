package com.jparkkennaby.store.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.jparkkennaby.store.dtos.ProductDto;
import com.jparkkennaby.store.entities.Product;

@Mapper(componentModel = "spring") // so spring is made aware and can make beans at runtime
public interface ProductMapper {
    @Mapping(target = "categoryId", source = "category.id")
    ProductDto toDto(Product product);

    Product toEntity(ProductDto productDto);

    @Mapping(target = "id", ignore = true)
    void update(ProductDto productDto, @MappingTarget Product product);
}