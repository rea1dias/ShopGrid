package com.shopgrid.order.domain;

import com.shopgrid.order.common.enums.CancelReason;
import com.shopgrid.order.common.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID userId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private BigDecimal totalPrice;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @Column(nullable = true)
    private Instant deliveredAt;

    @OneToMany(
            mappedBy = "order",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @OneToMany(
            mappedBy = "order",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderStatusHistory> statusHistory = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private CancelReason cancelReason;

    @Column(nullable = true)
    private Integer paymentAttempts = 0;

    public Order(UUID userId,
                 OrderStatus status,
                 BigDecimal totalPrice,
                 List<OrderItem> items,
                 Instant createdAt,
                 Instant updatedAt
    ) {
        this.userId = userId;
        this.status = status;
        this.totalPrice = totalPrice;
        this.items = items;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }
}
