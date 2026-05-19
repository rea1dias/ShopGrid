package com.shopgrid.product.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 150)
    private String description;

    @ManyToMany(mappedBy = "categories")
    private List<Product> products = new ArrayList<>();
}
