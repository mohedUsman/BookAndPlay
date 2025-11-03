package com.booknplay.booking_services.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BookingRequestDto {

    @NotNull
    private Long userId;

    @NotNull
    private Long turfId;

    @NotEmpty
    private List<@NotNull Long> slotIds;
}