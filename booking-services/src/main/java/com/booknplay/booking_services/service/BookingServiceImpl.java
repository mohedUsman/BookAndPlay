package com.booknplay.booking_services.service;

import com.booknplay.booking_services.client.TurfClient;
import com.booknplay.booking_services.dto.BookingRequestDto;
import com.booknplay.booking_services.dto.BookingResponseDto;
import com.booknplay.booking_services.dto.TurfDto;
import com.booknplay.booking_services.entity.Booking;
import com.booknplay.booking_services.entity.Slot;
import com.booknplay.booking_services.event.BookingCreatedEvent;
import com.booknplay.booking_services.exception.CustomException;
import com.booknplay.booking_services.repository.BookingRepository;
import com.booknplay.booking_services.repository.SlotRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao. OptimisticLockingFailureException;
// NEW: Publisher to emit events post-commit
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final SlotRepository slotRepository;
    private final TurfClient turfClient;
    private final ApplicationEventPublisher eventPublisher; // NEW: Injected publisher

    @Override
    @Transactional
    public BookingResponseDto createBooking(@Valid BookingRequestDto dto, Long userId) {
        if (userId == null) {
            throw new CustomException("userId is required");
        }
        if (dto.getTurfId() == null || dto.getSlotIds() == null || dto.getSlotIds().isEmpty()) {
            throw new CustomException("turfId and slotIds are required");
        }

        List<Slot> slots = slotRepository.findAllById(dto.getSlotIds());
        if (slots.size() != dto.getSlotIds().size()) {
            throw new CustomException("Some slot IDs are invalid");
        }
        Long turfId = dto.getTurfId();
        for (Slot slot : slots) {
            if (!slot.getTurfId().equals(turfId)) {
                throw new CustomException("Slot " + slot.getId() + " does not belong to turf " + turfId);
            }
            if (Boolean.TRUE.equals(slot.getIsBooked())) {
                throw new CustomException("Slot " + slot.getId() + " is already booked");
            }
        }

        // Price computation
        TurfDto turf = turfClient.getTurfById(turfId);

        // CHANGED: Parallelize duration/amount computation when slot list is large
        // NEW: Threshold guard to avoid overhead for small lists
        boolean parallelize = slots.size() >= 50; // NEW
        double totalHours = (parallelize
                ? slots.parallelStream()
                : slots.stream())
                .mapToDouble(s -> Duration.between(s.getStartTime(), s.getEndTime()).toMinutes() / 60.0)
                .sum(); // CHANGED
        double totalAmount = totalHours * turf.getPricePerHour();

        // Book the slots
        slots.forEach(s -> s.setIsBooked(true));
        try {
            slotRepository.saveAll(slots);
        } catch (OptimisticLockingFailureException e) {
            throw new CustomException("Concurrent booking detected. Please retry.");
        }

        // Create and save booking
        Booking booking = Booking.builder()
                .userId(userId)
                .turfId(turfId)
                .slots(slots)
                .bookingTime(LocalDateTime.now())
                .amount(totalAmount)
                .build();

        Booking saved = bookingRepository.save(booking);

        eventPublisher.publishEvent(new BookingCreatedEvent(saved.getId(), saved.getUserId(), saved.getTurfId(), saved.getAmount()));

        return toDto(saved);
    }

    @Override
    public BookingResponseDto getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new CustomException("No booking found"));
        return toDto(booking);
    }

    @Override
    public List<BookingResponseDto> getBookingsByUserId(Long userId) {
        // Optional: could parallelize mapping for large lists; keeping unchanged for simplicity
        return bookingRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<BookingResponseDto> getAllBookings() {
        // Optional: could parallelize mapping for large lists; keeping unchanged for simplicity
        return bookingRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    private BookingResponseDto toDto(Booking booking) {
        List<Long> slotIds = booking.getSlots().stream().map(Slot::getId).toList();
        return BookingResponseDto.builder()
                .bookingId(booking.getId())
                .userId(booking.getUserId())
                .turfId(booking.getTurfId())
                .totalAmount(booking.getAmount())
                .slotIds(slotIds)
                .bookingTime(booking.getBookingTime())
                .build();
    }
}