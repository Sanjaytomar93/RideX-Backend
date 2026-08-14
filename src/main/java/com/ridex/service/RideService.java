package com.ridex.service;

import com.ridex.dto.request.CancelRideRequest;
import com.ridex.dto.request.RideRequest;
import com.ridex.dto.response.PageResponse;
import com.ridex.dto.response.RideResponse;
import org.springframework.data.domain.Pageable;

public interface RideService {

    RideResponse requestRide(RideRequest request);

    RideResponse getRideById(Long rideId);

    RideResponse cancelRide(Long rideId, CancelRideRequest request);

    PageResponse<RideResponse> getRideHistory(Pageable pageable);
}
