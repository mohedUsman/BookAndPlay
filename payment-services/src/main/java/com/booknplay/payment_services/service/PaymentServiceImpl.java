    package com.booknplay.payment_services.service;

    import com.booknplay.payment_services.client.BookingClient;
    import com.booknplay.payment_services.client.NotificationClient;
    import com.booknplay.payment_services.client.UserClient;
    import com.booknplay.payment_services.dto.*;
    import com.booknplay.payment_services.entity.Payment;
    import com.booknplay.payment_services.entity.PaymentStatus;
    import com.booknplay.payment_services.exception.CustomException;
    import com.booknplay.payment_services.repository.PaymentRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import org.springframework.transaction.support.TransactionSynchronization;
    import org.springframework.transaction.support.TransactionSynchronizationManager;

    import java.util.List;
    import java.util.concurrent.CompletableFuture;
    import java.util.concurrent.TimeUnit;

    import static java.util.concurrent.CompletableFuture.supplyAsync;

    @Service
    @RequiredArgsConstructor
    public class PaymentServiceImpl implements PaymentService {

        private final PaymentRepository paymentRepository;
        private final BookingClient bookingClient;
        private final UserClient userClient;
        private final NotificationClient notificationClient;
        private final ThreadPoolTaskExecutor appTaskExecutor;

        @Override
        @Transactional
        public PaymentResponseDto initiatePayment(PaymentRequestDto request, String principalEmail) {
            if (request.getBookingId() == null) {
                throw new CustomException("bookingId is required");
            }

            // Parallel calls: resolve principal user and booking concurrently
            CompletableFuture<UserDto> principalFuture =
                    supplyAsync(() -> userClient.getUserByEmail(principalEmail), appTaskExecutor);

            CompletableFuture<BookingDto> bookingFuture =
                    supplyAsync(() -> bookingClient.getBookingById(request.getBookingId()), appTaskExecutor);

            UserDto principalUser = joinOrThrow(principalFuture, 6 , "Authenticated user not found or timed out check");
            if (principalUser == null || principalUser.getId() == null) {
                throw new CustomException("Authenticated user not found");
            }

            BookingDto booking = joinOrThrow(bookingFuture, 30, "Booking not found or timed out");
            if (booking == null) {
                throw new CustomException("Booking not found");
            }

            // Authorization: only booking owner can pay
            if (!booking.getUserId().equals(principalUser.getId())) {
                throw new CustomException("You can only pay for your own booking");
            }

            // Determine amount
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

            Long turfOwnerId = null; // TODO: populate when owner resolution is available

            // Single-threaded transactional write
            Payment payment = Payment.builder()
                    .bookingId(booking.getBookingId())
                    .payerUserId(principalUser.getId())
                    .turfOwnerId(turfOwnerId)
                    .amount(amount)
                    .status(PaymentStatus.SUCCESS) // simulate immediate success
                    .build();

            Payment saved = paymentRepository.save(payment);

            // After-commit async: publish notification or call downstream without blocking the request
            registerAfterCommit(() ->
                    CompletableFuture.runAsync(() -> {
                        try {
                            PaymentSuccessNotificationRequest notifReq = new PaymentSuccessNotificationRequest();
                            notifReq.setPaymentId(saved.getId());
                            notifReq.setBookingId(saved.getBookingId());
                            notifReq.setTurfId(booking.getTurfId());
                            notifReq.setRecipientUserId(principalUser.getId());
                            notifReq.setTurfOwnerId(turfOwnerId);
                            notifReq.setMessage("Payment #" + saved.getId() + " completed for booking #" + saved.getBookingId() + ".");
                            notificationClient.paymentSuccess(notifReq);
                            System.out.printf("Async: payment success notification for paymentId=%d bookingId=%d%n",
                                    saved.getId(), saved.getBookingId());
                        } catch (Exception e) {
                            // Log and consider retry with backoff or queue
                            System.err.println("Async notification failed: " + e.getMessage());
                        }
                    }, appTaskExecutor)
            );

            return toResponse(saved);
        }

        @Override
        public PaymentResponseDto getPaymentById(Long paymentId, String principalEmail) {
            UserDto principal = userClient.getUserByEmail(principalEmail);
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new CustomException("Payment not found"));

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

        // ----------------- helpers -----------------

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

        private static <T> T joinOrThrow(CompletableFuture<T> f, int seconds, String notFoundMessage) {
            try {
                T result = f.get(seconds, TimeUnit.SECONDS);
                if (result == null) throw new CustomException(notFoundMessage);
                return result;
            } catch (CustomException ce) {
                f.cancel(true);
                throw ce;
            } catch (Exception e) {
                f.cancel(true);
                throw new CustomException(notFoundMessage);
            }
        }

        private static void registerAfterCommit(Runnable r) {
            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override public void afterCommit() { r.run(); }
                });
            } else {
                r.run();
            }
        }
    }
