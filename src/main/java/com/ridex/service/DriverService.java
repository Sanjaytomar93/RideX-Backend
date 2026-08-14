package com.ridex.service;

import com.ridex.dto.request.DriverRegistrationRequest;
import com.ridex.dto.request.UpdateDriverLocationRequest;
import com.ridex.dto.request.UpdateDriverStatusRequest;
import com.ridex.dto.response.DriverProfileResponse;

public interface DriverService {

    DriverProfileResponse register(DriverRegistrationRequest request);

    DriverProfileResponse getProfile();

    DriverProfileResponse updateStatus(UpdateDriverStatusRequest request);

    DriverProfileResponse updateLocation(UpdateDriverLocationRequest request);
}
