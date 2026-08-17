package com.ridex.serviceImpl;

import com.ridex.entity.Driver;
import com.ridex.entity.Ride;
import com.ridex.enums.DriverStatus;
import com.ridex.repository.DriverRepository;
import com.ridex.service.RideNotificationService;
import com.ridex.util.DistanceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RideNotificationServiceImpl implements RideNotificationService {

    private static final double MAX_PICKUP_RADIUS_KM = 10.0;

    private final DriverRepository driverRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional(readOnly = true)
    public void notifyEligibleDrivers(Ride ride) {

        driverRepository.findByStatusAndDeletedFalse(DriverStatus.ONLINE).stream()
                .filter(this::isEligibleDriver)
                .filter(driver -> driver.getVehicle().getVehicleType() == ride.getVehicleType())
                .filter(driver -> isWithinPickupRadius(driver, ride))
                .forEach(driver -> messagingTemplate.convertAndSend(
                        "/topic/driver/" + driver.getId() + "/ride-requests",
                        RideServiceImpl.mapToResponse(ride)
                ));
    }

    private boolean isEligibleDriver(Driver driver) {
        return Boolean.TRUE.equals(driver.getIsVerified())
                && driver.getVehicle() != null
                && driver.getCurrentLatitude() != null
                && driver.getCurrentLongitude() != null;
    }

    private boolean isWithinPickupRadius(Driver driver, Ride ride) {
        double distanceToPickup = DistanceUtil.calculateKm(
                driver.getCurrentLatitude(),
                driver.getCurrentLongitude(),
                ride.getPickupLatitude(),
                ride.getPickupLongitude()
        );

        return distanceToPickup <= MAX_PICKUP_RADIUS_KM;
    }
}
