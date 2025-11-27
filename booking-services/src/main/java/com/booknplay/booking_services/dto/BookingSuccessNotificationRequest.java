package com.booknplay.booking_services.dto;

public record BookingSuccessNotificationRequest(
        Long bookingId,
        Long userId,
        Long turfId,
        Double totalAmount
) {}
