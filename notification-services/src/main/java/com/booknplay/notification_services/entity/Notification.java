package com.booknplay.notification_services.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationType type;

    @Column
    private Long bookingId;

    @Column
    private Long paymentId;

    @Column(nullable = false)
    private Long turfId;

    @Column(nullable = false)
    private Long recipientUserId; // booking user

    @Column(nullable = false)
    private Long turfOwnerId; // owner of the turf

    @Column(nullable = false, length = 500)
    private String message;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
