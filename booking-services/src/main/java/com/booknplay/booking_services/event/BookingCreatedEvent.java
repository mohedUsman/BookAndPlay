package com.booknplay.booking_services.event;

// NEW: Simple event payload to carry booking context post-commit
public record BookingCreatedEvent(
        Long bookingId, Long userId, Long turfId, Double totalAmount) {}