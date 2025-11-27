package com.booknplay.payment_services.event;

import com.booknplay.payment_services.client.NotificationClient; // NEW
import com.booknplay.payment_services.dto.PaymentSuccessNotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor // NEW: constructor injection
public class PaymentEventListener {

    private final NotificationClient notificationClient; // NEW: Feign client

    @Async("paymentAsyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @EventListener
    public void onPaymentCreated(PaymentCreatedEvent event) {
        // CHANGED: Send async notification via Notification Service
        try {
            var request = new PaymentSuccessNotificationRequest(
                    event.paymentId(), event.bookingId(), event.payerUserId(), event.amount()
            ); // NEW
            notificationClient.sendPaymentSuccess(request); // NEW
            log.info("[ASYNC] Payment notification sent: paymentId={}", event.paymentId()); // NEW
        } catch (Exception ex) {
            // NEW: Do not rethrow; log and continue to avoid impacting main flow
            log.error("[ASYNC] Failed to send payment notification: paymentId={}, error={}",
                    event.paymentId(), ex.getMessage(), ex);
        }
    }
}