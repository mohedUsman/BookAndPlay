package com.booknplay.payment_services.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PaymentRequestDto {
    @NotNull(message = "bookingId is required")
    private Long bookingId;

    // Optional override of amount for simulation; if provided, must be positive
    @Positive(message = "amount must be positive")
    private Double amount;
}