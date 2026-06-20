package com.shopgrid.payment.event;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_event")
@Getter
@Setter
@NoArgsConstructor
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private boolean sent = false;

    @CreationTimestamp
    private Instant createdAt;

    private Instant sentAt;

    public OutboxEvent(String eventType, String payload) {
        this.eventType = eventType;
        this.payload = payload;
    }

    public void markSent() {
        this.sent = true;
        this.sentAt = Instant.now();
    }
}