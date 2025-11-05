package com.booknplay.notification_services.repository;

import com.booknplay.notification_services.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientUserIdOrderByCreatedAtDesc(Long userId);
    List<Notification> findByTurfOwnerIdOrderByCreatedAtDesc(Long ownerId);
}
