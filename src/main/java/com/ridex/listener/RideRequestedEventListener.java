package com.ridex.listener;

import com.ridex.entity.Ride;
import com.ridex.event.RideRequestedEvent;
import com.ridex.exception.RideNotFoundException;
import com.ridex.repository.RideRepository;
import com.ridex.service.RideNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class RideRequestedEventListener {

    private final RideRepository rideRepository;
    private final RideNotificationService rideNotificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(readOnly = true)
    public void onRideRequested(RideRequestedEvent event) {

        Ride ride = rideRepository.findByIdAndDeletedFalse(event.rideId())
                .orElseThrow(() -> new RideNotFoundException("Ride not found"));

        rideNotificationService.notifyEligibleDrivers(ride);
    }
}
