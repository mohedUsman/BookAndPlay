package com.booknplay.payment_services.service;



import com.booknplay.payment_services.dto.PaymentRequestDto;
import com.booknplay.payment_services.dto.PaymentResponseDto;

import java.util.List;

public interface PaymentService {
    PaymentResponseDto initiatePayment(PaymentRequestDto request, String principalEmail);
    PaymentResponseDto getPaymentById(Long paymentId, String principalEmail);
    List<PaymentResponseDto> getMyPayments(String principalEmail);
    List<PaymentResponseDto> getOwnerPayments(String principalEmail);
    List<PaymentResponseDto> getAllPayments(String principalEmail);
}