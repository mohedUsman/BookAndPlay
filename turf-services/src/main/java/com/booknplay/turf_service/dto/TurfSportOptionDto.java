package com.booknplay.turf_service.dto;

import com.booknplay.turf_service.entity.SportType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TurfSportOptionDto { // CHANGE
    @NotNull(message = "sportType is required")
    private SportType sportType;

    @NotNull(message = "isIndoor is required")
    private Boolean isIndoor;
}