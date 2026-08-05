package com.ridex.service;
import com.ridex.dto.request.VerifyOtpRequest;
import com.ridex.dto.response.LoginResponse;

public interface AuthService {

    void sendOtp(String mobileNumber);

    LoginResponse verifyOtp(VerifyOtpRequest request);
}
