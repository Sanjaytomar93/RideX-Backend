package com.ridex.serviceImpl;
import com.ridex.constants.ResponseMessage;
import com.ridex.dto.request.CancelRideRequest;
import com.ridex.dto.request.RideRequest;
import com.ridex.dto.response.PageResponse;
import com.ridex.dto.response.RideResponse;
import com.ridex.entity.Ride;
import com.ridex.entity.User;
import com.ridex.event.RideRequestedEvent;
import com.ridex.enums.PaymentStatus;
import com.ridex.enums.RideStatus;
import com.ridex.exception.ActiveRideExistsException;
import com.ridex.exception.ResourceNotFoundException;
import com.ridex.exception.RideNotFoundException;
import com.ridex.repository.RideRepository;
import com.ridex.repository.UserRepository;
import com.ridex.service.RideService;
import com.ridex.util.DistanceUtil;
import com.ridex.util.FareCalculator;
import com.ridex.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    private static final Set<RideStatus> ACTIVE_RIDE_STATUSES = Set.of(
            RideStatus.REQUESTED,
            RideStatus.DRIVER_ASSIGNED,
            RideStatus.DRIVER_ARRIVED,
            RideStatus.RIDE_STARTED
    );

    private static final Set<RideStatus> CANCELLABLE_STATUSES = Set.of(
            RideStatus.REQUESTED,
            RideStatus.DRIVER_ASSIGNED
    );

    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public RideResponse requestRide(RideRequest request) {

        User user = getFreshUserFromDb();

        if (rideRepository.existsByUserIdAndStatusInAndDeletedFalse(user.getId(), ACTIVE_RIDE_STATUSES)) {
            throw new ActiveRideExistsException(ResponseMessage.ACTIVE_RIDE_EXISTS);
        }

        double distanceKm = DistanceUtil.calculateKm(
                request.getPickupLatitude(),
                request.getPickupLongitude(),
                request.getDropLatitude(),
                request.getDropLongitude()
        );

        double fare = FareCalculator.calculateFare(request.getVehicleType(), distanceKm);

        Ride ride = Ride.builder()
                .user(user)
                .vehicleType(request.getVehicleType())
                .pickupAddress(request.getPickupAddress())
                .pickupLatitude(request.getPickupLatitude())
                .pickupLongitude(request.getPickupLongitude())
                .dropAddress(request.getDropAddress())
                .dropLatitude(request.getDropLatitude())
                .dropLongitude(request.getDropLongitude())
                .distanceKm(distanceKm)
                .fare(fare)
                .status(RideStatus.REQUESTED)
                .paymentStatus(PaymentStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();

        Ride savedRide = rideRepository.save(ride);

        eventPublisher.publishEvent(new RideRequestedEvent(savedRide.getId()));

        return mapToResponse(savedRide);
    }

    @Override
    @Transactional(readOnly = true)
    public RideResponse getRideById(Long rideId) {

        User user = getFreshUserFromDb();
        Ride ride = getRideOrThrow(rideId);

        if (!ride.getUser().getId().equals(user.getId())) {
            throw new RideNotFoundException(ResponseMessage.RIDE_NOT_FOUND);
        }

        return mapToResponse(ride);
    }

    @Override
    @Transactional
    public RideResponse cancelRide(Long rideId, CancelRideRequest request) {

        User user = getFreshUserFromDb();
        Ride ride = getRideOrThrow(rideId);

        if (!ride.getUser().getId().equals(user.getId())) {
            throw new RideNotFoundException(ResponseMessage.RIDE_NOT_FOUND);
        }

        if (!CANCELLABLE_STATUSES.contains(ride.getStatus())) {
            throw new IllegalArgumentException(ResponseMessage.RIDE_CANNOT_BE_CANCELLED);
        }

        ride.setStatus(RideStatus.CANCELLED);
        ride.setCancellationReason(request.getReason());

        Ride cancelledRide = rideRepository.save(ride);

        return mapToResponse(cancelledRide);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<RideResponse> getRideHistory(Pageable pageable) {

        User user = getFreshUserFromDb();

        Page<Ride> ridePage = rideRepository
                .findByUserIdAndDeletedFalseOrderByCreatedAtDesc(user.getId(), pageable);

        return PageResponse.<RideResponse>builder()
                .content(ridePage.getContent().stream().map(RideServiceImpl::mapToResponse).toList())
                .page(ridePage.getNumber())
                .size(ridePage.getSize())
                .totalElements(ridePage.getTotalElements())
                .totalPages(ridePage.getTotalPages())
                .last(ridePage.isLast())
                .build();
    }

    static RideResponse mapToResponse(Ride ride) {

        RideResponse.RideResponseBuilder builder = RideResponse.builder()
                .id(ride.getId())
                .userId(ride.getUser().getId())
                .userName(ride.getUser().getFullName())
                .userMobile(ride.getUser().getMobileNumber())
                .vehicleType(ride.getVehicleType())
                .pickupAddress(ride.getPickupAddress())
                .pickupLatitude(ride.getPickupLatitude())
                .pickupLongitude(ride.getPickupLongitude())
                .dropAddress(ride.getDropAddress())
                .dropLatitude(ride.getDropLatitude())
                .dropLongitude(ride.getDropLongitude())
                .distanceKm(ride.getDistanceKm())
                .fare(ride.getFare())
                .status(ride.getStatus())
                .paymentStatus(ride.getPaymentStatus())
                .requestedAt(ride.getRequestedAt())
                .completedAt(ride.getCompletedAt())
                .cancellationReason(ride.getCancellationReason());

        if (ride.getDriver() != null) {
            builder.driverId(ride.getDriver().getId())
                    .driverName(ride.getDriver().getFullName())
                    .driverMobile(ride.getDriver().getMobileNumber());
        }

        return builder.build();
    }

    private Ride getRideOrThrow(Long rideId) {

        return rideRepository.findByIdAndDeletedFalse(rideId)
                .orElseThrow(() -> new RideNotFoundException(ResponseMessage.RIDE_NOT_FOUND));
    }

    private User getFreshUserFromDb() {

        User currentUser = SecurityUtil.getCurrentUser();

        return userRepository.findByIdAndDeletedFalse(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
