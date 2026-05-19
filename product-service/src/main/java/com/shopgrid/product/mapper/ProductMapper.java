package com.shopgrid.product.mapper;

import com.shopgrid.product.domain.dto.request.ProductRequest;
import com.shopgrid.product.domain.dto.response.ProductResponse;
import com.shopgrid.product.domain.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "sellerId", ignore = true)
    @Mapping(target = "categories", ignore = true)
    Product toEntity(ProductRequest request);

    ProductResponse toResponse(Product product);


}
