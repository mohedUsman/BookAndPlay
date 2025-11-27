package com.booknplay.booking_services.event;

import com.booknplay.booking_services.client.NotificationClient; // NEW
import com.booknplay.booking_services.dto.BookingSuccessNotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor // NEW: constructor injection
public class BookingEventListener {

    private final NotificationClient notificationClient; // NEW: Feign client

    @Async("bookingAsyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @EventListener
    public void onBookingCreated(BookingCreatedEvent event) {
        // CHANGED: Send async notification via Notification Service
        try {
            var request = new BookingSuccessNotificationRequest(
                    event.bookingId(), event.userId(), event.turfId(), event.totalAmount()
            ); // NEW
            notificationClient.sendBookingSuccess(request); // NEW
            log.info("[ASYNC] Booking notification sent: bookingId={}", event.bookingId()); // NEW
        } catch (Exception ex) {
            // NEW: Do not rethrow; log and continue to avoid impacting main flow
            log.error("[ASYNC] Failed to send booking notification: bookingId={}, error={}",
                    event.bookingId(), ex.getMessage(), ex);
        }
    }
}