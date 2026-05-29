package com.shopgrid.search.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.math.BigDecimal;
import java.util.List;

@Document(indexName = "products")
@Getter
@Setter
@AllArgsConstructor
public class ProductDocument {

    @Id
    private String id;

    private String name;

    private String description;

    private BigDecimal price;

    private String sku;

    private String status;

    private List<String> categories;
}
