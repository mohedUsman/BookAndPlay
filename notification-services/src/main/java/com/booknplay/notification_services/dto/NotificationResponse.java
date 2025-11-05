package com.booknplay.notification_services.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {
    private Long id;
    private String type;
    private Long bookingId;
    private Long paymentId;
    private Long turfId;
    private Long recipientUserId;
    private Long turfOwnerId;
    private String message;
    private LocalDateTime createdAt;
}
