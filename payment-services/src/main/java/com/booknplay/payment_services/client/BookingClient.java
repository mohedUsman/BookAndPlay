package com.booknplay.payment_services.client;


import com.booknplay.payment_services.config.FeignClientConfig;
import com.booknplay.payment_services.dto.BookingDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "BOOKING-SERVICE", configuration = FeignClientConfig.class, path = "/api/bookings")
public interface BookingClient {
    @GetMapping("/{bookingId}")
    BookingDto getBookingById(@PathVariable Long bookingId);
}
