package com.booknplay.payment_services.dto;

// NEW: DTO used for notification request from Payment Service
public record PaymentSuccessNotificationRequest(
        Long paymentId,
        Long bookingId,
        Long payerUserId,
        Double amount
) {}