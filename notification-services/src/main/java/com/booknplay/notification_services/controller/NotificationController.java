package com.booknplay.notification_services.controller;

import com.booknplay.notification_services.dto.BookingSuccessNotificationRequest;
import com.booknplay.notification_services.dto.NotificationResponse;
import com.booknplay.notification_services.dto.PaymentSuccessNotificationRequest;
import com.booknplay.notification_services.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Trigger booking success notification")
    @PostMapping("/booking-success")
    public ResponseEntity<NotificationResponse> bookingSuccess(@Valid @RequestBody BookingSuccessNotificationRequest request,
                                                               @AuthenticationPrincipal Jwt principal){
        String email = principal.getSubject();
        NotificationResponse response = notificationService.notifyBookingSuccess(request, email);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "Trigger payment success notification")
    @PostMapping("/payment-success")
    public ResponseEntity<NotificationResponse> paymentSuccess(@Valid @RequestBody PaymentSuccessNotificationRequest request,
                                                               @AuthenticationPrincipal Jwt principal){
        String email = principal.getSubject();
        NotificationResponse response = notificationService.notifyPaymentSuccess(request, email);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "List my notifications (ROLE_USER)")
    @GetMapping("/me")
    public ResponseEntity<List<NotificationResponse>> my(@AuthenticationPrincipal Jwt principal){
        String email = principal.getSubject();
        return ResponseEntity.ok(notificationService.getMyNotifications(email));
    }

    @Operation(summary = "List owner notifications (ROLE_OWNER or ROLE_ADMIN)")
    @GetMapping("/owner")
    public ResponseEntity<List<NotificationResponse>> owner(@AuthenticationPrincipal Jwt principal){
        String email = principal.getSubject();
        return ResponseEntity.ok(notificationService.getOwnerNotifications(email));
    }

    @Operation(summary = "List all notifications (ROLE_ADMIN)")
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> all(@AuthenticationPrincipal Jwt principal){
        String email = principal.getSubject();
        return ResponseEntity.ok(notificationService.getAllNotifications(email));
    }
}
