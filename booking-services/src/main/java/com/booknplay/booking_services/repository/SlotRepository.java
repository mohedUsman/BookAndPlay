package com.booknplay.booking_services.repository;

import com.booknplay.booking_services.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface SlotRepository extends JpaRepository<Slot, Long> {
    // Removed specialized finders that supported uniqueness or pre-checks for idempotency:
    // Optional<Slot> findByTurfIdAndDateAndStartTimeAndEndTime(...);
}