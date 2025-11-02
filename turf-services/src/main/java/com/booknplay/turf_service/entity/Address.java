package com.booknplay.turf_service.entity;

import jakarta.persistence. Embeddable;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class Address { // CHANGE: new embeddable

    private String street;
    private String city;

}