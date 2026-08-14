package com.ridex.service;

import com.ridex.dto.response.PageResponse;
import com.ridex.dto.response.RideResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DriverRideService {

    List<RideResponse> getAvailableRideRequests();

    RideResponse acceptRide(Long rideId);

    RideResponse markArrived(Long rideId);

    RideResponse startRide(Long rideId);

    RideResponse completeRide(Long rideId);

    PageResponse<RideResponse> getRideHistory(Pageable pageable);
}
