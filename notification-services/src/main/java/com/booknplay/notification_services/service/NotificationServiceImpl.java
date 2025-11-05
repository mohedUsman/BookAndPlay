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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserClient userClient;

    @Override
    public NotificationResponse notifyBookingSuccess(BookingSuccessNotificationRequest request, String principalEmail) {
        validateIds(request.getRecipientUserId(), request.getTurfOwnerId(), request.getTurfId());
        Notification notification = Notification.builder()
                .type(NotificationType.BOOKING_SUCCESS)
                .bookingId(request.getBookingId())
                .turfId(request.getTurfId())
                .recipientUserId(request.getRecipientUserId())
                .turfOwnerId(request.getTurfOwnerId())
                .message(request.getMessage() != null
                        ? request.getMessage()
                        : "Booking #" + request.getBookingId() + " confirmed.")
                .build();

        return toResponse(notificationRepository.save(notification));
    }

    @Override
    public NotificationResponse notifyPaymentSuccess(PaymentSuccessNotificationRequest request, String principalEmail) {
        validateIds(request.getRecipientUserId(), request.getTurfOwnerId(), request.getTurfId());
        Notification notification = Notification.builder()
                .type(NotificationType.PAYMENT_SUCCESS)
                .paymentId(request.getPaymentId())
                .bookingId(request.getBookingId())
                .turfId(request.getTurfId())
                .recipientUserId(request.getRecipientUserId())
                .turfOwnerId(request.getTurfOwnerId())
                .message(request.getMessage() != null
                        ? request.getMessage()
                        : "Payment #" + request.getPaymentId() + " completed for booking #" + request.getBookingId() + ".")
                .build();

        return toResponse(notificationRepository.save(notification));
    }

    @Override
    public List<NotificationResponse> getMyNotifications(String principalEmail) {
        UserDto user = userClient.getUserByEmail(principalEmail);
        if (user == null || user.getId() == null) throw new CustomException("Authenticated user not found");
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
}