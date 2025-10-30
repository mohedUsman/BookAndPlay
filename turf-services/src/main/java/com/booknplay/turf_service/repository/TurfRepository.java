package com.booknplay.turf_service.repository;

import com.booknplay.turf_service.entity.Turf;
import com.booknplay.turf_service.entity.TurfStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TurfRepository extends JpaRepository<Turf, Long> {
    List<Turf> findByStatus(TurfStatus turfStatus);
    List<Turf> findByOwnerId(Long ownerId);
}
