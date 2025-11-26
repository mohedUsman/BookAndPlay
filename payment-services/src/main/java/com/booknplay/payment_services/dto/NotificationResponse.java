package com.booknplay.payment_services.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
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
