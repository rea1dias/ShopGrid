package com.shopgrid.order.domain;

import com.shopgrid.order.common.enums.RefundReason;
import com.shopgrid.order.common.enums.RefundStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "refund")
@Getter
@Setter
@NoArgsConstructor
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID orderId;

    private UUID userId;

    @Enumerated(EnumType.STRING)
    private RefundStatus status;

    @OneToMany(mappedBy = "refund", cascade = CascadeType.ALL)
    private List<RefundItem> refundItems = new ArrayList<>();

    @CreationTimestamp
    private Instant refundCreatedAt;

    @Enumerated(EnumType.STRING)
    private RefundReason reason;

    private BigDecimal refundAmount;

    @Column(nullable = true)
    private String rejectionReason;

    @Column(nullable = true)
    private Instant resolvedAt;

    private String comment;

    public Refund(UUID orderId, UUID userId, RefundStatus status, List<RefundItem> refundItems, RefundReason reason, BigDecimal refundAmount, String comment) {
        this.orderId = orderId;
        this.userId = userId;
        this.status = status;
        this.refundItems = refundItems;
        this.reason = reason;
        this.refundAmount = refundAmount;
        this.comment = comment;
        this.refundCreatedAt = Instant.now();
    }
}
