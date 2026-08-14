package com.ridex.controller;

import com.ridex.constants.ResponseMessage;
import com.ridex.dto.request.DriverRegistrationRequest;
import com.ridex.dto.request.UpdateDriverLocationRequest;
import com.ridex.dto.request.UpdateDriverStatusRequest;
import com.ridex.dto.response.CommonApiResponse;
import com.ridex.dto.response.DriverProfileResponse;
import com.ridex.service.DriverService;
import com.ridex.util.ApiResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/driver")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @PostMapping("/register")
    public ResponseEntity<CommonApiResponse<DriverProfileResponse>> registerDriver(
            @Valid @RequestBody DriverRegistrationRequest request) {

        DriverProfileResponse response = driverService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseUtil.success(
                        HttpStatus.CREATED.value(),
                        ResponseMessage.DRIVER_REGISTERED,
                        response
                ));
    }

    @GetMapping("/profile")
    public ResponseEntity<CommonApiResponse<DriverProfileResponse>> getDriverProfile() {

        DriverProfileResponse response = driverService.getProfile();

        return ResponseEntity.ok(ApiResponseUtil.success(
                HttpStatus.OK.value(),
                ResponseMessage.DRIVER_PROFILE_FETCHED,
                response
        ));
    }

    @PutMapping("/status")
    public ResponseEntity<CommonApiResponse<DriverProfileResponse>> updateDriverStatus(
            @Valid @RequestBody UpdateDriverStatusRequest request) {

        DriverProfileResponse response = driverService.updateStatus(request);

        return ResponseEntity.ok(ApiResponseUtil.success(
                HttpStatus.OK.value(),
                ResponseMessage.DRIVER_STATUS_UPDATED,
                response
        ));
    }

    @PutMapping("/location")
    public ResponseEntity<CommonApiResponse<DriverProfileResponse>> updateDriverLocation(
            @Valid @RequestBody UpdateDriverLocationRequest request) {

        DriverProfileResponse response = driverService.updateLocation(request);

        return ResponseEntity.ok(ApiResponseUtil.success(
                HttpStatus.OK.value(),
                ResponseMessage.DRIVER_LOCATION_UPDATED,
                response
        ));
    }
}
