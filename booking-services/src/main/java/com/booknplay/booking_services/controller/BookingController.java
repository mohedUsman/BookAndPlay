package com.booknplay.booking_services.controller;


import com.booknplay.booking_services.client.UserClient;
import com.booknplay.booking_services.dto.BookingRequestDto;
import com.booknplay.booking_services.dto.BookingResponseDto;
import com.booknplay.booking_services.dto.UserDto;
import com.booknplay.booking_services.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final UserClient userClient;

    @Operation(
            summary = "Create a booking",
            description = "Creates a booking for the provided userId/turfId and slotIds. " +
                    "No principal-based user lookup; userId is trusted from request.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(@Valid @RequestBody BookingRequestDto dto,
                                                            @AuthenticationPrincipal Jwt principal) {
        UserDto user = userClient.getUserByEmail(principal.getSubject());
        BookingResponseDto response = bookingService.createBooking(dto, user.getId());
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "Get booking by ID")
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> getBookingById(@PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.getBookingById(bookingId));
    }

    @Operation(summary = "Get bookings by user")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingResponseDto>> getBookingsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getBookingsByUserId(userId));
    }

    @Operation(summary = "Get all bookings")
    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }
}
