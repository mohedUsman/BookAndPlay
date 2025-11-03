package com.booknplay.booking_services.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class SlotDto {
    private Long id;

    @NotNull
    private Long turfId;

    @NotNull
    @FutureOrPresent
    private LocalDate date;

    @NotNull
    private LocalTime from;

    @NotNull
    private LocalTime to;

    private boolean booked;
}
