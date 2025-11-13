package com.booknplay.payment_services.service;

import com.booknplay.payment_services.client.BookingClient;
import com.booknplay.payment_services.client.UserClient;
import com.booknplay.payment_services.dto.BookingDto;
import com.booknplay.payment_services.dto.PaymentRequestDto;
import com.booknplay.payment_services.dto.PaymentResponseDto;
import com.booknplay.payment_services.dto.UserDto;
import com.booknplay.payment_services.entity.Payment;
import com.booknplay.payment_services.entity.PaymentStatus;
import com.booknplay.payment_services.exception.CustomException;
import com.booknplay.payment_services.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingClient bookingClient;
    private final UserClient userClient;

    @Override
    public PaymentResponseDto initiatePayment(PaymentRequestDto request, String principalEmail) {
        if (request.getBookingId() == null) {
            throw new CustomException("bookingId is required");
        }

        // Resolve principal as user
        UserDto principalUser = userClient.getUserByEmail(principalEmail);
        if (principalUser == null || principalUser.getId() == null) {
            throw new CustomException("Authenticated user not found");
        }

        // Load booking
        BookingDto booking = bookingClient.getBookingById(request.getBookingId());
        if (booking == null) {
            throw new CustomException("Booking not found");
        }

        // Authorization: only booking owner can pay
        if (!booking.getUserId().equals(principalUser.getId())) {
            throw new CustomException("You can only pay for your own booking");
        }

        // Determine amount: use booking.totalAmount if request.amount is null; else validate request.amount
        double amount = booking.getTotalAmount() != null ? booking.getTotalAmount() : 0.0;
        if (request.getAmount() != null) {
            if (request.getAmount() <= 0) {
                throw new CustomException("Provided amount must be positive");
            }
            amount = request.getAmount();
        }
        if (amount <= 0) {
            throw new CustomException("Amount to pay must be greater than zero");
        }


        Long turfOwnerId = null; // placeholder, see controller-level owner listing requiring ownerId resolution

        Payment payment = Payment.builder()
                .bookingId(booking.getBookingId())
                .payerUserId(principalUser.getId())
                .turfOwnerId(turfOwnerId)
                .amount(amount)
                .status(PaymentStatus.SUCCESS) // simulate successful charge
                .build();

        Payment saved = paymentRepository.save(payment);
        return toResponse(saved);
    }

    @Override
    public PaymentResponseDto getPaymentById(Long paymentId, String principalEmail) {
        UserDto principal = userClient.getUserByEmail(principalEmail);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CustomException("Payment not found"));

        // Visibility: admin can view all; owner can view if ownerId matches; user can view own
        if (isAdmin(principal)) {
            return toResponse(payment);
        }
        if (isOwner(principal) && payment.getTurfOwnerId() != null && principal.getId().equals(payment.getTurfOwnerId())) {
            return toResponse(payment);
        }
        if (payment.getPayerUserId().equals(principal.getId())) {
            return toResponse(payment);
        }
        throw new CustomException("Not authorized to view this payment");
    }

    @Override
    public List<PaymentResponseDto> getMyPayments(String principalEmail) {
        UserDto principal = userClient.getUserByEmail(principalEmail);
        return paymentRepository.findByPayerUserId(principal.getId())
                .stream().map(this::toResponse).toList();
    }

    @Override
    public List<PaymentResponseDto> getOwnerPayments(String principalEmail) {
        UserDto principal = userClient.getUserByEmail(principalEmail);
        if (!isOwner(principal) && !isAdmin(principal)) {
            throw new CustomException("Only owners or admins can view owner payments");
        }
        // If we had turfOwnerId stored properly, filter by that id
        return paymentRepository.findByTurfOwnerId(principal.getId())
                .stream().map(this::toResponse).toList();
    }

    @Override
    public List<PaymentResponseDto> getAllPayments(String principalEmail) {
        UserDto principal = userClient.getUserByEmail(principalEmail);
        if (!isAdmin(principal)) {
            throw new CustomException("Only admins can view all payments");
        }
        return paymentRepository.findAll().stream().map(this::toResponse).toList();
    }

    private boolean isAdmin(UserDto user) {
        return user != null && user.getRoles() != null &&
                user.getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equalsIgnoreCase(r.getName()));
    }

    private boolean isOwner(UserDto user) {
        return user != null && user.getRoles() != null &&
                user.getRoles().stream().anyMatch(r -> "ROLE_OWNER".equalsIgnoreCase(r.getName()));
    }

    private PaymentResponseDto toResponse(Payment p) {
        return PaymentResponseDto.builder()
                .paymentId(p.getId())
                .bookingId(p.getBookingId())
                .payerUserId(p.getPayerUserId())
                .turfOwnerId(p.getTurfOwnerId())
                .amount(p.getAmount())
                .status(p.getStatus().name())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
