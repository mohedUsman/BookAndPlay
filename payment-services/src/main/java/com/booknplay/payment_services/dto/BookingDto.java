package com.booknplay.payment_services.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookingDto {
    private Long bookingId;
    private Long userId;
    private Long turfId;
    private Double totalAmount;
    private List<Long> slotIds;
    private LocalDateTime bookingTime;
}