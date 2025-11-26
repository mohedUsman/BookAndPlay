package com.booknplay.notification_services.service;

import com.booknplay.notification_services.client.UserClient;
import com.booknplay.notification_services.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.CompletableFuture.supplyAsync;

@Component
@RequiredArgsConstructor
public class NotificationEnrichment {

    private final UserClient userClient;
    private final ThreadPoolTaskExecutor appTaskExecutor;

    public String enrichMessageForBooking(Long recipientUserId, Long turfOwnerId, Long bookingId, String baseMessage) {
        CompletableFuture<UserDto> recipientF =
                supplyAsync(() -> userClient.getUserByIdSafe(recipientUserId), appTaskExecutor);
        CompletableFuture<UserDto> ownerF =
                supplyAsync(() -> userClient.getUserByIdSafe(turfOwnerId), appTaskExecutor);

        UserDto recipient = join(recipientF, 3);
        UserDto owner = join(ownerF, 3);

        String recipientName = recipient != null ? recipient.getName() : "User";
        String ownerName = owner != null ? owner.getName() : "Owner";

        return String.format("%s Hi %s. Owner: %s.", baseMessage, recipientName, ownerName);
    }

    public String enrichMessageForPayment(Long recipientUserId, Long turfOwnerId, Long bookingId, Long paymentId, String baseMessage) {
        CompletableFuture<UserDto> recipientF =
                supplyAsync(() -> userClient.getUserByIdSafe(recipientUserId), appTaskExecutor);
        CompletableFuture<UserDto> ownerF =
                supplyAsync(() -> userClient.getUserByIdSafe(turfOwnerId), appTaskExecutor);

        UserDto recipient = join(recipientF, 3);
        UserDto owner = join(ownerF, 3);

        String recipientName = recipient != null ? recipient.getName() : "User";
        String ownerName = owner != null ? owner.getName() : "Owner";

        return String.format("%s Hi %s. Owner: %s.", baseMessage, recipientName, ownerName);
    }

    private static <T> T join(CompletableFuture<T> f, int seconds) {
        try { return f.get(seconds, TimeUnit.SECONDS); }
        catch (Exception e) { f.cancel(true); return null; }
    }
}
