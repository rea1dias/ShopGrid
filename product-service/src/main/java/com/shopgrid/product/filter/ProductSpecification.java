package com.shopgrid.product.filter;

import com.shopgrid.product.common.ProductStatus;
import com.shopgrid.product.domain.dto.request.ProductFilterRequest;
import com.shopgrid.product.domain.entity.Product;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.UUID;

@UtilityClass
public class ProductSpecification {

    public Specification<Product> filter(ProductFilterRequest request) {
        return Specification.where(byName(request.name()))
                .and(byCategory(request.categoryId()))
                .and(byMinPrice(request.minPrice()))
                .and(byMaxPrice(request.maxPrice()))
                .and(byStatus(request.status()));
    }

    private Specification<Product> byName(String name) {
        return (root, query, cb) -> name == null ? null :
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private Specification<Product> byCategory(UUID categoryId) {
        return (root, query, cb) -> categoryId == null ? null :
                cb.equal(root.join("categories").get("id"), categoryId);
    }

    private Specification<Product> byMinPrice(BigDecimal minPrice) {
        return ((root, query, cb) -> minPrice == null ? null :
                cb.greaterThanOrEqualTo(root.get("price"), minPrice));
    }

    private Specification<Product> byMaxPrice(BigDecimal maxPrice) {
        return (root, query, cb) -> maxPrice == null ? null :
                cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    private Specification<Product> byStatus(ProductStatus status) {
        return (root, query, cb) -> status == null ? null :
                cb.equal(root.get("status"), status);
    }

}
