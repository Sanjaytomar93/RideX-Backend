package com.ridex.service;

import com.ridex.dto.request.RejectDriverRequest;
import com.ridex.dto.response.DriverProfileResponse;
import com.ridex.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface AdminDriverService {

    PageResponse<DriverProfileResponse> getPendingDrivers(Pageable pageable);

    DriverProfileResponse getDriverById(Long driverId);

    DriverProfileResponse verifyDriver(Long driverId);

    DriverProfileResponse rejectDriver(Long driverId, RejectDriverRequest request);
}
