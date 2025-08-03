package com.product.manager.mapper;

import com.product.manager.dto.ProductDto;
import com.product.manager.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDto toDto(Product product);

    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductDto productDto);
}
