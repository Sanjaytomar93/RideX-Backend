package com.ridex.service;

import com.ridex.dto.request.SendOtpRequest;
import com.ridex.dto.request.VerifyOtpRequest;
import com.ridex.dto.response.DriverLoginResponse;

public interface DriverAuthService {

    void sendOtp(SendOtpRequest request);

    DriverLoginResponse verifyOtp(VerifyOtpRequest request);
}
