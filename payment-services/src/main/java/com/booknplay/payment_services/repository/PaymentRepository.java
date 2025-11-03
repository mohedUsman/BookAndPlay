package com.booknplay.payment_services.repository;

import com.booknplay.payment_services.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByPayerUserId(Long userId);
    List<Payment> findByTurfOwnerId(Long ownerId);
}