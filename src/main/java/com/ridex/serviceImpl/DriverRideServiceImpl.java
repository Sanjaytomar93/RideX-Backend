package com.ridex.serviceImpl;

import com.ridex.constants.ResponseMessage;
import com.ridex.dto.response.PageResponse;
import com.ridex.dto.response.RideResponse;
import com.ridex.entity.Driver;
import com.ridex.entity.Ride;
import com.ridex.entity.Vehicle;
import com.ridex.enums.DriverStatus;
import com.ridex.enums.PaymentStatus;
import com.ridex.enums.RideStatus;
import com.ridex.exception.ActiveRideExistsException;
import com.ridex.exception.ResourceNotFoundException;
import com.ridex.exception.RideNotFoundException;
import com.ridex.repository.DriverRepository;
import com.ridex.repository.RideRepository;
import com.ridex.service.DriverRideService;
import com.ridex.util.DistanceUtil;
import com.ridex.util.DriverSecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DriverRideServiceImpl implements DriverRideService {

    private static final double MAX_PICKUP_RADIUS_KM = 10.0;

    private static final Set<RideStatus> ACTIVE_DRIVER_RIDE_STATUSES = Set.of(
            RideStatus.DRIVER_ASSIGNED,
            RideStatus.DRIVER_ARRIVED,
            RideStatus.RIDE_STARTED
    );

    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RideResponse> getAvailableRideRequests() {

        Driver driver = getFreshDriverFromDb();

        validateDriverCanAcceptRides(driver);

        Vehicle vehicle = driver.getVehicle();
        if (vehicle == null) {
            throw new IllegalArgumentException(ResponseMessage.DRIVER_VEHICLE_NOT_FOUND);
        }

        return rideRepository
                .findByStatusAndVehicleTypeAndDeletedFalse(RideStatus.REQUESTED, vehicle.getVehicleType())
                .stream()
                .filter(ride -> isWithinPickupRadius(driver, ride))
                .map(RideServiceImpl::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public RideResponse acceptRide(Long rideId) {

        Driver driver = getFreshDriverFromDb();
        validateDriverCanAcceptRides(driver);

        if (rideRepository.findByDriverIdAndStatusInAndDeletedFalse(
                driver.getId(), ACTIVE_DRIVER_RIDE_STATUSES).isPresent()) {
            throw new ActiveRideExistsException(ResponseMessage.DRIVER_ACTIVE_RIDE_EXISTS);
        }

        Ride ride = rideRepository.findByIdAndDeletedFalse(rideId)
                .orElseThrow(() -> new RideNotFoundException(ResponseMessage.RIDE_NOT_FOUND));

        if (ride.getStatus() != RideStatus.REQUESTED) {
            throw new IllegalArgumentException(ResponseMessage.RIDE_NOT_AVAILABLE);
        }

        if (driver.getVehicle() == null
                || ride.getVehicleType() != driver.getVehicle().getVehicleType()) {
            throw new IllegalArgumentException(ResponseMessage.RIDE_VEHICLE_TYPE_MISMATCH);
        }

        ride.setDriver(driver);
        ride.setStatus(RideStatus.DRIVER_ASSIGNED);

        driver.setStatus(DriverStatus.ON_RIDE);

        driverRepository.save(driver);

        Ride acceptedRide = rideRepository.save(ride);

        return RideServiceImpl.mapToResponse(acceptedRide);
    }

    @Override
    @Transactional
    public RideResponse markArrived(Long rideId) {

        Driver driver = getFreshDriverFromDb();
        Ride ride = getAssignedRideOrThrow(rideId, driver);

        if (ride.getStatus() != RideStatus.DRIVER_ASSIGNED) {
            throw new IllegalArgumentException(ResponseMessage.INVALID_RIDE_STATUS_TRANSITION);
        }

        ride.setStatus(RideStatus.DRIVER_ARRIVED);

        return RideServiceImpl.mapToResponse(rideRepository.save(ride));
    }

    @Override
    @Transactional
    public RideResponse startRide(Long rideId) {

        Driver driver = getFreshDriverFromDb();
        Ride ride = getAssignedRideOrThrow(rideId, driver);

        if (ride.getStatus() != RideStatus.DRIVER_ARRIVED) {
            throw new IllegalArgumentException(ResponseMessage.INVALID_RIDE_STATUS_TRANSITION);
        }

        ride.setStatus(RideStatus.RIDE_STARTED);

        return RideServiceImpl.mapToResponse(rideRepository.save(ride));
    }

    @Override
    @Transactional
    public RideResponse completeRide(Long rideId) {

        Driver driver = getFreshDriverFromDb();
        Ride ride = getAssignedRideOrThrow(rideId, driver);

        if (ride.getStatus() != RideStatus.RIDE_STARTED) {
            throw new IllegalArgumentException(ResponseMessage.INVALID_RIDE_STATUS_TRANSITION);
        }

        ride.setStatus(RideStatus.RIDE_COMPLETED);
        ride.setPaymentStatus(PaymentStatus.PENDING);
        ride.setCompletedAt(LocalDateTime.now());

        driver.setStatus(DriverStatus.ONLINE);
        driver.setTotalRides(driver.getTotalRides() + 1);

        driverRepository.save(driver);

        return RideServiceImpl.mapToResponse(rideRepository.save(ride));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<RideResponse> getRideHistory(Pageable pageable) {

        Driver driver = getFreshDriverFromDb();

        Page<Ride> ridePage = rideRepository
                .findByDriverIdAndDeletedFalseOrderByCreatedAtDesc(driver.getId(), pageable);

        return PageResponse.<RideResponse>builder()
                .content(ridePage.getContent().stream().map(RideServiceImpl::mapToResponse).toList())
                .page(ridePage.getNumber())
                .size(ridePage.getSize())
                .totalElements(ridePage.getTotalElements())
                .totalPages(ridePage.getTotalPages())
                .last(ridePage.isLast())
                .build();
    }

    private void validateDriverCanAcceptRides(Driver driver) {

        if (!Boolean.TRUE.equals(driver.getIsVerified())) {
            throw new IllegalArgumentException(ResponseMessage.DRIVER_NOT_VERIFIED);
        }

        if (driver.getStatus() != DriverStatus.ONLINE) {
            throw new IllegalArgumentException(ResponseMessage.DRIVER_MUST_BE_ONLINE);
        }
    }

    private boolean isWithinPickupRadius(Driver driver, Ride ride) {

        if (driver.getCurrentLatitude() == null || driver.getCurrentLongitude() == null) {
            return true;
        }

        double distanceToPickup = DistanceUtil.calculateKm(
                driver.getCurrentLatitude(),
                driver.getCurrentLongitude(),
                ride.getPickupLatitude(),
                ride.getPickupLongitude()
        );

        return distanceToPickup <= MAX_PICKUP_RADIUS_KM;
    }

    private Ride getAssignedRideOrThrow(Long rideId, Driver driver) {

        Ride ride = rideRepository.findByIdAndDeletedFalse(rideId)
                .orElseThrow(() -> new RideNotFoundException(ResponseMessage.RIDE_NOT_FOUND));

        if (ride.getDriver() == null || !ride.getDriver().getId().equals(driver.getId())) {
            throw new RideNotFoundException(ResponseMessage.RIDE_NOT_FOUND);
        }

        return ride;
    }

    private Driver getFreshDriverFromDb() {

        Driver currentDriver = DriverSecurityUtil.getCurrentDriver();

        return driverRepository.findByIdAndDeletedFalse(currentDriver.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessage.DRIVER_NOT_FOUND));
    }
}
