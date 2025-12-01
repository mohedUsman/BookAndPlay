package com.booknplay.booking_services.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
public class BookingEventListener {

    @Async("bookingAsyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @EventListener
    public void onBookingCreated(BookingCreatedEvent event) {

        log.info("[ASYNC] Booking created: id={}, userId={}, turfId={}, totalAmount={}",
                event.bookingId(), event.userId(), event.turfId(), event.totalAmount());
    }
}