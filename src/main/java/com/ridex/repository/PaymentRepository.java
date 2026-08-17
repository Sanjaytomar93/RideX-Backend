package com.ridex.repository;

import com.ridex.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ridex.entity.Payment;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByRazorpayOrderIdAndDeletedFalse(String razorpayOrderId);

    Optional<Payment> findFirstByRideIdAndStatusAndDeletedFalseOrderByCreatedAtDesc(
            Long rideId, PaymentStatus status);

    Page<Payment> findByUserIdAndDeletedFalseOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
