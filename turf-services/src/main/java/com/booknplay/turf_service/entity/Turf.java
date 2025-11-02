package com.booknplay.turf_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "turf")
@Getter
@Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Turf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ownerId;

    @Embedded
    private Address address; // street + city only

    @Column(nullable = false, length = 255)
    private String name;

    @Column
    private Double pricePerHour;

    @Column(nullable = false)
    private LocalTime availableFrom;

    @Column(nullable = false)
    private LocalTime availableTo;

    // IMPORTANT: Turf no longer contains is_indoor or sport_type.
    // Multiple sport options are stored in child table.
    @OneToMany(mappedBy = "turf", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<TurfSportOption> sportOptions = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public void setSportOptions(List<TurfSportOption> options) {
        if (this.sportOptions == null) {
            this.sportOptions = new ArrayList<>();
        }
        this.sportOptions.clear();
        if (options != null) {
            for (TurfSportOption opt : options) {
                opt.setTurf(this);
            }
            this.sportOptions.addAll(options);
        }
    }
}
