package com.booknplay.payment_services.controller;


import com.booknplay.payment_services.dto.PaymentRequestDto;
import com.booknplay.payment_services.dto.PaymentResponseDto;
import com.booknplay.payment_services.service.PaymentService;
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
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "Initiate payment for a booking (user only)")
    @PostMapping
    public ResponseEntity<PaymentResponseDto> pay(@Valid @RequestBody PaymentRequestDto request,
                                                  @AuthenticationPrincipal Jwt principal) {
        String email = principal.getSubject();
        PaymentResponseDto response = paymentService.initiatePayment(request, email);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "Get payment by ID (user self, owner, or admin)")
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDto> get(@PathVariable Long paymentId,
                                                  @AuthenticationPrincipal Jwt principal) {
        String email = principal.getSubject();
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId, email));
    }

    @Operation(summary = "List my payments (user)")
    @GetMapping("/me")
    public ResponseEntity<List<PaymentResponseDto>> my(@AuthenticationPrincipal Jwt principal) {
        String email = principal.getSubject();
        return ResponseEntity.ok(paymentService.getMyPayments(email));
    }

    @Operation(summary = "List owner payments (owner or admin)")
    @GetMapping("/owner")
    public ResponseEntity<List<PaymentResponseDto>> owner(@AuthenticationPrincipal Jwt principal) {
        String email = principal.getSubject();
        return ResponseEntity.ok(paymentService.getOwnerPayments(email));
    }

    @Operation(summary = "List all payments (admin)")
    @GetMapping
    public ResponseEntity<List<PaymentResponseDto>> all(@AuthenticationPrincipal Jwt principal) {
        String email = principal.getSubject();
        return ResponseEntity.ok(paymentService.getAllPayments(email));
    }
}