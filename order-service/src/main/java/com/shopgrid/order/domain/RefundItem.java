package com.shopgrid.order.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "refund_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefundItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "refund_id")
    private Refund refund;

    private UUID orderItemId;
    private UUID productId;
    private Integer quantity;
    private BigDecimal price;
}
