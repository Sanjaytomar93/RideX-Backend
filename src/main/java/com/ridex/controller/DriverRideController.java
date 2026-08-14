package com.ridex.controller;

import com.ridex.constants.ResponseMessage;
import com.ridex.dto.response.CommonApiResponse;
import com.ridex.dto.response.PageResponse;
import com.ridex.dto.response.RideResponse;
import com.ridex.service.DriverRideService;
import com.ridex.util.ApiResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/driver/rides")
@RequiredArgsConstructor
public class DriverRideController {

    private final DriverRideService driverRideService;

    @GetMapping("/requests")
    public ResponseEntity<CommonApiResponse<List<RideResponse>>> getAvailableRideRequests() {

        List<RideResponse> response = driverRideService.getAvailableRideRequests();

        return ResponseEntity.ok(ApiResponseUtil.success(
                HttpStatus.OK.value(),
                ResponseMessage.RIDE_REQUESTS_FETCHED,
                response
        ));
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<CommonApiResponse<RideResponse>> acceptRide(
            @PathVariable Long id) {

        RideResponse response = driverRideService.acceptRide(id);

        return ResponseEntity.ok(ApiResponseUtil.success(
                HttpStatus.OK.value(),
                ResponseMessage.RIDE_ACCEPTED,
                response
        ));
    }

    @PutMapping("/{id}/arrived")
    public ResponseEntity<CommonApiResponse<RideResponse>> markArrived(
            @PathVariable Long id) {

        RideResponse response = driverRideService.markArrived(id);

        return ResponseEntity.ok(ApiResponseUtil.success(
                HttpStatus.OK.value(),
                ResponseMessage.RIDE_DRIVER_ARRIVED,
                response
        ));
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<CommonApiResponse<RideResponse>> startRide(
            @PathVariable Long id) {

        RideResponse response = driverRideService.startRide(id);

        return ResponseEntity.ok(ApiResponseUtil.success(
                HttpStatus.OK.value(),
                ResponseMessage.RIDE_STARTED,
                response
        ));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<CommonApiResponse<RideResponse>> completeRide(
            @PathVariable Long id) {

        RideResponse response = driverRideService.completeRide(id);

        return ResponseEntity.ok(ApiResponseUtil.success(
                HttpStatus.OK.value(),
                ResponseMessage.RIDE_COMPLETED,
                response
        ));
    }

    @GetMapping("/history")
    public ResponseEntity<CommonApiResponse<PageResponse<RideResponse>>> getRideHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        PageResponse<RideResponse> response = driverRideService.getRideHistory(pageable);

        return ResponseEntity.ok(ApiResponseUtil.success(
                HttpStatus.OK.value(),
                ResponseMessage.RIDE_HISTORY_FETCHED,
                response
        ));
    }
}
