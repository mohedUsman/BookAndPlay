package com.booknplay.payment_services.dto;

import lombok.Data;

@Data
public class PaymentSuccessNotificationRequest {
    private Long paymentId;
    private Long bookingId;
    private Long turfId;
    private Long recipientUserId;
    private Long turfOwnerId;
    private String message;
}
