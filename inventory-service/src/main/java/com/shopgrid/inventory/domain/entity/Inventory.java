package com.shopgrid.inventory.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "inventories")
@Getter
@Setter
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID productId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer reserved;

    public Inventory(UUID productId, Integer quantity, Integer reserved) {
        this.productId = productId;
        this.quantity = quantity;
        this.reserved = reserved;
    }
    protected Inventory() {}
}
