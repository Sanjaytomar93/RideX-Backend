package com.ridex.repository;

import com.ridex.entity.Ride;
import com.ridex.enums.RideStatus;
import com.ridex.enums.VehicleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

    Optional<Ride> findByIdAndDeletedFalse(Long id);

    Page<Ride> findByUserIdAndDeletedFalseOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<Ride> findByDriverIdAndDeletedFalseOrderByCreatedAtDesc(Long driverId, Pageable pageable);

    List<Ride> findByStatusAndVehicleTypeAndDeletedFalse(RideStatus status, VehicleType vehicleType);

    boolean existsByUserIdAndStatusInAndDeletedFalse(Long userId, Collection<RideStatus> statuses);

    Optional<Ride> findByDriverIdAndStatusInAndDeletedFalse(Long driverId, Collection<RideStatus> statuses);
}
