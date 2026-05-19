package com.shopgrid.product.mapper;

import com.shopgrid.product.domain.dto.request.CategoryRequest;
import com.shopgrid.product.domain.dto.response.CategoryResponse;
import com.shopgrid.product.domain.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    Category toEntity(CategoryRequest request);

    CategoryResponse toResponse(Category category);
}
