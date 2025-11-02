package com.booknplay.turf_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "turf_sport_option")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TurfSportOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SportType sportType;

    @Column(nullable = false)
    private Boolean isIndoor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turf_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Turf turf;
}