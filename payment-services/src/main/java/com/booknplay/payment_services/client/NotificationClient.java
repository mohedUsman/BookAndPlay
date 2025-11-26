package com.booknplay.payment_services.client;

import com.booknplay.payment_services.config.FeignClientConfig;
import com.booknplay.payment_services.dto.NotificationResponse;
import com.booknplay.payment_services.dto.PaymentSuccessNotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "NOTIFICATION-SERVICE", configuration = FeignClientConfig.class, path = "/api/notifications")
public interface NotificationClient {

    @PostMapping("/payment-success")
    NotificationResponse paymentSuccess(PaymentSuccessNotificationRequest request);
}