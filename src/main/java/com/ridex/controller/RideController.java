package com.ridex.controller;

import com.ridex.constants.ResponseMessage;
import com.ridex.dto.request.CancelRideRequest;
import com.ridex.dto.request.RideRequest;
import com.ridex.dto.response.CommonApiResponse;
import com.ridex.dto.response.PageResponse;
import com.ridex.dto.response.RideResponse;
import com.ridex.service.RideService;
import com.ridex.util.ApiResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ride")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    @PostMapping("/request")
    public ResponseEntity<CommonApiResponse<RideResponse>> requestRide(
            @Valid @RequestBody RideRequest request) {

        RideResponse response = rideService.requestRide(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseUtil.success(
                        HttpStatus.CREATED.value(),
                        ResponseMessage.RIDE_REQUESTED,
                        response
                ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonApiResponse<RideResponse>> getRideById(
            @PathVariable Long id) {

        RideResponse response = rideService.getRideById(id);

        return ResponseEntity.ok(ApiResponseUtil.success(
                HttpStatus.OK.value(),
                ResponseMessage.RIDE_FETCHED,
                response
        ));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<CommonApiResponse<RideResponse>> cancelRide(
            @PathVariable Long id,
            @RequestBody(required = false) CancelRideRequest request) {

        CancelRideRequest cancelRequest = request != null ? request : new CancelRideRequest();

        RideResponse response = rideService.cancelRide(id, cancelRequest);

        return ResponseEntity.ok(ApiResponseUtil.success(
                HttpStatus.OK.value(),
                ResponseMessage.RIDE_CANCELLED,
                response
        ));
    }

    @GetMapping("/history")
    public ResponseEntity<CommonApiResponse<PageResponse<RideResponse>>> getRideHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        PageResponse<RideResponse> response = rideService.getRideHistory(pageable);

        return ResponseEntity.ok(ApiResponseUtil.success(
                HttpStatus.OK.value(),
                ResponseMessage.RIDE_HISTORY_FETCHED,
                response
        ));
    }
}
