package com.booknplay.payment_services.event;

// NEW: Simple event payload to carry payment context post-commit
public record PaymentCreatedEvent(
        Long paymentId, Long bookingId, Long payerUserId, Double amount) {}
