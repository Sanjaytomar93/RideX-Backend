package com.ridex.controller;

import com.ridex.constants.ResponseMessage;
import com.ridex.dto.request.RejectDriverRequest;
import com.ridex.dto.response.CommonApiResponse;
import com.ridex.dto.response.DriverProfileResponse;
import com.ridex.dto.response.PageResponse;
import com.ridex.service.AdminDriverService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/drivers")
@RequiredArgsConstructor
public class AdminDriverController {

    private final AdminDriverService adminDriverService;

    @GetMapping("/pending")
    public ResponseEntity<CommonApiResponse<PageResponse<DriverProfileResponse>>> getPendingDrivers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        PageResponse<DriverProfileResponse> response =
                adminDriverService.getPendingDrivers(pageable);

        return ResponseEntity.ok(ApiResponseUtil.success(
                HttpStatus.OK.value(),
                ResponseMessage.PENDING_DRIVERS_FETCHED,
                response
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonApiResponse<DriverProfileResponse>> getDriverById(
            @PathVariable Long id) {

        DriverProfileResponse response = adminDriverService.getDriverById(id);

        return ResponseEntity.ok(ApiResponseUtil.success(
                HttpStatus.OK.value(),
                ResponseMessage.DRIVER_PROFILE_FETCHED,
                response
        ));
    }

    @PutMapping("/{id}/verify")
    public ResponseEntity<CommonApiResponse<DriverProfileResponse>> verifyDriver(
            @PathVariable Long id) {

        DriverProfileResponse response = adminDriverService.verifyDriver(id);

        return ResponseEntity.ok(ApiResponseUtil.success(
                HttpStatus.OK.value(),
                ResponseMessage.DRIVER_VERIFIED,
                response
        ));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<CommonApiResponse<DriverProfileResponse>> rejectDriver(
            @PathVariable Long id,
            @Valid @RequestBody RejectDriverRequest request) {

        DriverProfileResponse response = adminDriverService.rejectDriver(id, request);

        return ResponseEntity.ok(ApiResponseUtil.success(
                HttpStatus.OK.value(),
                ResponseMessage.DRIVER_REJECTED,
                response
        ));
    }
}
