package com.ridex.controller;
import com.ridex.dto.request.VerifyOtpRequest;
import com.ridex.dto.response.CommonApiResponse;
import com.ridex.dto.response.LoginResponse;
import com.ridex.service.AuthService;
import com.ridex.util.ApiResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/verify-otp")
    public ResponseEntity<CommonApiResponse<LoginResponse>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {

        LoginResponse response = authService.verifyOtp(request);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        HttpStatus.OK.value(),
                        "Login Successful",
                        response
                )
        );
    }

    @PostMapping("/send-otp")
    public ResponseEntity<CommonApiResponse<String>> sendOtp(
            @RequestParam String mobileNumber) {

        authService.sendOtp(mobileNumber);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        HttpStatus.OK.value(),
                        "OTP Sent Successfully",
                        null
                )
        );
    }
}