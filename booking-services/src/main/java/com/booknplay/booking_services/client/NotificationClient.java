package com.booknplay.booking_services.client;

import com.booknplay.booking_services.dto.BookingSuccessNotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "NOTIFICATION-SERVICE") // NEW: Uses Eureka service name
public interface NotificationClient {

    @PostMapping("/api/notifications/booking-success") // NEW: Endpoint in Notification Service
    void sendBookingSuccess(@RequestBody BookingSuccessNotificationRequest request);
}