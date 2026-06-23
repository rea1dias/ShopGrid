package com.shopgrid.notification.common.entity;

import com.shopgrid.notification.common.enums.ChannelType;
import com.shopgrid.notification.common.enums.NotificationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "event_id", unique = true, nullable = false)
    private String eventId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "channel_type", nullable = false)
    private ChannelType channel;

    @Column(name = "recipient", nullable = false)
    private String recipient;

    @Column(name = "message_text", length = 2000)
    private String messageText;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NotificationStatus status;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public Notification(String eventId,
                        String userId,
                        ChannelType channel,
                        String recipient,
                        String messageText,
                        NotificationStatus status,
                        String errorMessage,
                        Instant createdAt,
                        Instant updatedAt) {
        this.eventId = eventId;
        this.userId = userId;
        this.channel = channel;
        this.recipient = recipient;
        this.messageText = messageText;
        this.status = status;
        this.errorMessage = errorMessage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
