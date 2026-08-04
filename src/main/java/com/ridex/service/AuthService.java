package com.ridex.service;
import com.ridex.dto.request.VerifyOtpRequest;
import com.ridex.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse verifyOtp(VerifyOtpRequest request);
}
