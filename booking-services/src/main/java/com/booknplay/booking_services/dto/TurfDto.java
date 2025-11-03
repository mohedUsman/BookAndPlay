package com.booknplay.booking_services.dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class TurfDto {
    private Long id;
    private Long ownerId;
    private String name;
    private Double pricePerHour;
    private LocalTime availableFrom;
    private LocalTime availableTo;
    private AddressDto address; // aligns with simplified address in turf-service
}