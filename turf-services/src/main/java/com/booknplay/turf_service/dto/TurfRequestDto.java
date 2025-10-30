package com.booknplay.turf_service.dto;

import com.booknplay.turf_service.entity.SportType;
import com.booknplay.turf_service.entity.TurfStatus;
import lombok.Data;

import java.time.LocalTime;

@Data
public class TurfRequestDto {
    private String name;
    private String location;
    private SportType sportType;
    private Double pricePerHour;
    private Boolean isIndoor;
    private TurfStatus status;
    private LocalTime availableFrom;
    private LocalTime availableTo;
}
