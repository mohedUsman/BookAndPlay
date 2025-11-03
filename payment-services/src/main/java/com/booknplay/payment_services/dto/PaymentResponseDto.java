package com.booknplay.payment_services.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponseDto {
    private Long paymentId;
    private Long bookingId;
    private Long payerUserId;
    private Long turfOwnerId;
    private Double amount;
    private String status;
    private LocalDateTime createdAt;
}