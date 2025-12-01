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
public class PaymentEventListener {

    @Async("paymentAsyncExecutor") // NEW: Run asynchronously
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT) // NEW: post-commit
    @EventListener
    public void onPaymentCreated(PaymentCreatedEvent event) {
        // NEW: Simple audit log stub; replace with Notification Service call if desired
        log.info("[ASYNC] Payment created: id={}, bookingId={}, payerUserId={}, amount={}",
                event.paymentId(), event.bookingId(), event.payerUserId(), event.amount());
        // NEW: Here you can call Notification Service via Feign (optional), wrapped in try/catch
    }
}