package com.booknplay.turf_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

import java.util.List;

@Data
@Builder
public class TurfResponseDto { // CHANGE: new structure
    private Long id;
    private Long ownerId;
    private String name;
    private AddressDto address; // CHANGE
    private Double pricePerHour;
    private LocalTime availableFrom;
    private LocalTime availableTo;
    private List<TurfSportOptionDto> sportOptions; // CHANGE
}