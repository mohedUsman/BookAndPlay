package com.booknplay.booking_services.service;



import com.booknplay.booking_services.dto.BookingRequestDto;
import com.booknplay.booking_services.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(BookingRequestDto dto, Long userId);
    BookingResponseDto getBookingById(Long bookingId);
    List<BookingResponseDto> getBookingsByUserId(Long userId);
    List<BookingResponseDto> getAllBookings();
}
