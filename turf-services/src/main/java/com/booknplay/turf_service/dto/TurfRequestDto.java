package com.booknplay.turf_service.dto;

import lombok.Data;

import java.time.LocalTime;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

@Data
public class TurfRequestDto { // CHANGE: adjusted to new model
    @NotBlank(message = "name is required")
    @Size(max = 255, message = "name must be at most 255 characters")
    private String name;

    @Valid
    @NotNull(message = "address is required")
    private AddressDto address;

    @Valid
    @NotEmpty(message = "sportOptions is required and cannot be empty")
    private List<TurfSportOptionDto> sportOptions;

    @Positive(message = "pricePerHour must be positive")
    @NotNull(message = "pricePerHour is required")
    private Double pricePerHour;

    @NotNull(message = "availableFrom is required")
    private LocalTime availableFrom;

    @NotNull(message = "availableTo is required")
    private LocalTime availableTo;
}
