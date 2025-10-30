package com.booknplay.turf_service.dto;

import com.booknplay.turf_service.entity.SportType;
import com.booknplay.turf_service.entity.TurfStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
public class TurfResponseDto {
    private Long id;
    private Long ownerId;
    private String name;
    private String location;
    private SportType sportType;
    private Double pricePerHour;
    private boolean isIndoor;
    private LocalTime availableFrom;
    private LocalTime availableTo;
    private TurfStatus status;
}
