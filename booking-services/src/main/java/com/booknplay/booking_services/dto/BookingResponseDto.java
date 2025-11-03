package com.booknplay.booking_services.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;




@Data
@Builder
public class BookingResponseDto {
    private Long bookingId;
    private Long userId;
    private Long turfId;
    private Double totalAmount;
    private List<Long> slotIds;
    private LocalDateTime bookingTime;
}