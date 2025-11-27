package com.booknplay.payment_services.client;

import com.booknplay.payment_services.dto.PaymentSuccessNotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "NOTIFICATION-SERVICE") // NEW: Uses Eureka service name
public interface NotificationClient {

    @PostMapping("/api/notifications/payment-success") // NEW: Endpoint in Notification Service
    void sendPaymentSuccess(@RequestBody PaymentSuccessNotificationRequest request);
}