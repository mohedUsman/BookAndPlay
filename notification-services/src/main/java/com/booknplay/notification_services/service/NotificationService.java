package com.booknplay.notification_services.service;


import com.booknplay.notification_services.dto.BookingSuccessNotificationRequest;
import com.booknplay.notification_services.dto.NotificationResponse;
import com.booknplay.notification_services.dto.PaymentSuccessNotificationRequest;

import java.util.List;

public interface NotificationService {
    NotificationResponse notifyBookingSuccess(BookingSuccessNotificationRequest request, String principalEmail);
    NotificationResponse notifyPaymentSuccess(PaymentSuccessNotificationRequest request, String principalEmail);

    List<NotificationResponse> getMyNotifications(String principalEmail);
    List<NotificationResponse> getOwnerNotifications(String principalEmail);
    List<NotificationResponse> getAllNotifications(String principalEmail);
}