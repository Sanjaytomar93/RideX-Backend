package com.ridex.controller;
import com.ridex.constants.ResponseMessage;
import com.ridex.dto.request.SendOtpRequest;
import com.ridex.dto.request.VerifyOtpRequest;
import com.ridex.dto.response.CommonApiResponse;
import com.ridex.dto.response.LoginResponse;
import com.ridex.service.AuthService;
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
                        ResponseMessage.LOGIN_SUCCESS,
                        response
                )
        );
    }

    @PostMapping("/send-otp")
    public ResponseEntity<CommonApiResponse<String>> sendOtp(
            @Valid @RequestBody SendOtpRequest request) {

        authService.sendOtp(request.getMobileNumber());

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        HttpStatus.OK.value(),
                        ResponseMessage.OTP_SENT,
                        null
                )
        );
    }
}
