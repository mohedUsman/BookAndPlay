package com.booknplay.notification_services.service;


import com.booknplay.notification_services.client.UserClient;
import com.booknplay.notification_services.dto.BookingSuccessNotificationRequest;
import com.booknplay.notification_services.dto.NotificationResponse;
import com.booknplay.notification_services.dto.PaymentSuccessNotificationRequest;
import com.booknplay.notification_services.dto.UserDto;
import com.booknplay.notification_services.entity.Notification;
import com.booknplay.notification_services.entity.NotificationType;
import com.booknplay.notification_services.exception.CustomException;
import com.booknplay.notification_services.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.CompletableFuture.supplyAsync;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserClient userClient;
    private final ThreadPoolTaskExecutor appTaskExecutor;
    private final NotificationEnrichment enrichment;

    @Override
    @Transactional
    public NotificationResponse notifyBookingSuccess(BookingSuccessNotificationRequest request, String principalEmail) {
        validateIds(request.getRecipientUserId(), request.getTurfOwnerId(), request.getTurfId());

        // Parallel user checks (optional, fast-fail)
        CompletableFuture<UserDto> recipientF =
                supplyAsync(() -> userClient.getUserByIdSafe(request.getRecipientUserId()), appTaskExecutor);
        CompletableFuture<UserDto> ownerF =
                supplyAsync(() -> userClient.getUserByIdSafe(request.getTurfOwnerId()), appTaskExecutor);

        // Apply a fast timeout; do not block write if enrichment fails
        joinSilently(recipientF, 2);
        joinSilently(ownerF, 2);

        String baseMessage = request.getMessage() != null
                ? request.getMessage()
                : "Booking #" + request.getBookingId() + " confirmed.";
        String finalMessage = safeEnrich(() ->
                enrichment.enrichMessageForBooking(request.getRecipientUserId(), request.getTurfOwnerId(),
                        request.getBookingId(), baseMessage), baseMessage);

        Notification notification = Notification.builder()
                .type(NotificationType.BOOKING_SUCCESS)
                .bookingId(request.getBookingId())
                .turfId(request.getTurfId())
                .recipientUserId(request.getRecipientUserId())
                .turfOwnerId(request.getTurfOwnerId())
                .message(finalMessage)
                .build();

        return toResponse(notificationRepository.save(notification));
    }

    @Override
    @Transactional
    public NotificationResponse notifyPaymentSuccess(PaymentSuccessNotificationRequest request, String principalEmail) {
        validateIds(request.getRecipientUserId(), request.getTurfOwnerId(), request.getTurfId());

        CompletableFuture<UserDto> recipientF =
                supplyAsync(() -> userClient.getUserByIdSafe(request.getRecipientUserId()), appTaskExecutor);
        CompletableFuture<UserDto> ownerF =
                supplyAsync(() -> userClient.getUserByIdSafe(request.getTurfOwnerId()), appTaskExecutor);

        joinSilently(recipientF, 2);
        joinSilently(ownerF, 2);

        String baseMessage = request.getMessage() != null
                ? request.getMessage()
                : "Payment #" + request.getPaymentId() + " completed for booking #" + request.getBookingId() + ".";
        String finalMessage = safeEnrich(() ->
                enrichment.enrichMessageForPayment(request.getRecipientUserId(), request.getTurfOwnerId(),
                        request.getBookingId(), request.getPaymentId(), baseMessage), baseMessage);

        Notification notification = Notification.builder()
                .type(NotificationType.PAYMENT_SUCCESS)
                .paymentId(request.getPaymentId())
                .bookingId(request.getBookingId())
                .turfId(request.getTurfId())
                .recipientUserId(request.getRecipientUserId())
                .turfOwnerId(request.getTurfOwnerId())
                .message(finalMessage)
                .build();

        return toResponse(notificationRepository.save(notification));
    }

    @Override
    public List<NotificationResponse> getMyNotifications(String principalEmail) {
        UserDto user = userClient.getUserByEmail(principalEmail);
        if (user == null || user.getId() == null) throw new CustomException("Authenticated user not found");
        // IO is DB-bound; keep simple and synchronous
        return notificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(user.getId())
                .stream().map(this::toResponse).toList();
    }

    @Override
    public List<NotificationResponse> getOwnerNotifications(String principalEmail) {
        UserDto user = userClient.getUserByEmail(principalEmail);
        if (user == null || user.getId() == null) throw new CustomException("Authenticated user not found");
        boolean isOwner = user.getRoles() != null && user.getRoles().stream()
                .anyMatch(r -> "ROLE_OWNER".equalsIgnoreCase(r.getName()));
        boolean isAdmin = user.getRoles() != null && user.getRoles().stream()
                .anyMatch(r -> "ROLE_ADMIN".equalsIgnoreCase(r.getName()));
        if (!isOwner && !isAdmin) throw new CustomException("Only owners or admins can view owner notifications");

        return notificationRepository.findByTurfOwnerIdOrderByCreatedAtDesc(user.getId())
                .stream().map(this::toResponse).toList();
    }

    @Override
    public List<NotificationResponse> getAllNotifications(String principalEmail) {
        UserDto user = userClient.getUserByEmail(principalEmail);
        boolean isAdmin = user != null && user.getRoles() != null &&
                user.getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equalsIgnoreCase(r.getName()));
        if (!isAdmin) throw new CustomException("Only admins can view all notifications");
        return notificationRepository.findAll().stream().map(this::toResponse).toList();
    }

    private void validateIds(Long recipientUserId, Long turfOwnerId, Long turfId) {
        if (recipientUserId == null || turfOwnerId == null || turfId == null) {
            throw new CustomException("recipientUserId, turfOwnerId, and turfId are required");
        }
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .type(n.getType().name())
                .bookingId(n.getBookingId())
                .paymentId(n.getPaymentId())
                .turfId(n.getTurfId())
                .recipientUserId(n.getRecipientUserId())
                .turfOwnerId(n.getTurfOwnerId())
                .message(n.getMessage())
                .createdAt(n.getCreatedAt())
                .build();
    }

    private static <T> void joinSilently(CompletableFuture<T> f, int seconds) {
        try { f.get(seconds, TimeUnit.SECONDS); } catch (Exception e) { f.cancel(true); }
    }

    private static String safeEnrich(SupplierWithException<String> supplier, String fallback) {
        try { return supplier.get(); } catch (Exception e) { return fallback; }
    }

    @FunctionalInterface
    interface SupplierWithException<T> { T get() throws Exception; }
}