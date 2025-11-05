package com.booknplay.notification_services.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PaymentSuccessNotificationRequest {
    @NotNull @Positive
    private Long paymentId;

    @NotNull @Positive
    private Long bookingId;

    @NotNull @Positive
    private Long turfId;

    @NotNull @Positive
    private Long recipientUserId;

    @NotNull @Positive
    private Long turfOwnerId;

    private String message;
}
