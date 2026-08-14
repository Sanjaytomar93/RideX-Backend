package com.ridex.controller;

import com.ridex.constants.ResponseMessage;
import com.ridex.dto.request.SendOtpRequest;
import com.ridex.dto.request.VerifyOtpRequest;
import com.ridex.dto.response.CommonApiResponse;
import com.ridex.dto.response.DriverLoginResponse;
import com.ridex.service.DriverAuthService;
import com.ridex.util.ApiResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/driver/auth")
@RequiredArgsConstructor
public class DriverAuthController {

    private final DriverAuthService driverAuthService;

    @PostMapping("/send-otp")
    public ResponseEntity<CommonApiResponse<String>> sendOtp(
            @Valid @RequestBody SendOtpRequest request) {

        driverAuthService.sendOtp(request);

        return ResponseEntity.ok(ApiResponseUtil.success(
                HttpStatus.OK.value(),
                ResponseMessage.OTP_SENT,
                null
        ));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<CommonApiResponse<DriverLoginResponse>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {

        DriverLoginResponse response = driverAuthService.verifyOtp(request);

        return ResponseEntity.ok(ApiResponseUtil.success(
                HttpStatus.OK.value(),
                ResponseMessage.LOGIN_SUCCESS,
                response
        ));
    }
}
